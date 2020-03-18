package init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import _00_init.util.DBService;
import _00_init.util.GlobalService;
import _00_init.util.HibernateUtils;
import _01_register.model.MemberBean;
import _03_listBooks.model.BookBean;
import _03_listBooks.model.CompanyBean;

// 本程式建立範例程式所需要的表格與部分表格的初始資料
// 範例程式所需要的表格如下：
// 
public class CardTableResetHibernate {
	public static final String UTF8_BOM = "\uFEFF"; // 定義 UTF-8的BOM字元

	public static void main(String args[]) {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.openSession();
		Transaction tx = null;

		String line = "";
		int count = 0;
		// 1. BookCompany表格
		// 1-1 由"data/bookCompany.dat"逐筆讀入BookCompany表格內的初始資料，
		// 然後依序新增到BookCompany表格中
		try (
			FileReader fr = new FileReader("data/bookCompany.dat"); 
			BufferedReader br = new BufferedReader(fr);
		) {
			while ((line = br.readLine()) != null) {
				if (line.startsWith(UTF8_BOM)) {
					line = line.substring(1);
				}
				String[] token = line.split("\\|");
				String name = token[0];
				String address = token[1];
				String url = token[2];
				CompanyBean cb = new CompanyBean(null, name, address, url);
				try {
					tx = session.beginTransaction();
					session.save(cb);
					tx.commit();
				} catch (Exception ex) {
					if (tx != null)
						tx.rollback();
				}
			}
			System.out.println("BookCompany 資料新增成功");
		} catch (IOException e) {
			System.err.println("新建BookCompany表格時發生IO例外: " + e.getMessage());
		}

		// 2. Book表格
		File file = new File("data/book.dat");
		// 2-1 由"data/book.dat"逐筆讀入Book表格內的初始資料，然後依序新增
		// 到Book表格中
		try (
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			BufferedReader br = new BufferedReader(isr);
		) {
			while ((line = br.readLine()) != null) {
				System.out.println("line=" + line);
				// 去除 UTF8_BOM: \uFEFF
				if (line.startsWith(UTF8_BOM)) {
					line = line.substring(1);
				}
				String[] token = line.split("\\|");
				BookBean book = new BookBean();
				book.setTitle(token[0]);
				book.setAuthor(token[1]);
				book.setListPrice(Double.parseDouble(token[2].trim()));
				book.setDiscount(Double.parseDouble(token[3].trim()));
				book.setCompanyId(Integer.parseInt(token[4].trim()));
				// 讀取圖片檔
				Blob blob = GlobalService.fileToBlob(token[5].trim());
				book.setCoverImage(blob);
				book.setFileName(GlobalService.extractFileName(token[5].trim()));
				book.setBookNo(token[6].trim());
				book.setStock(Integer.parseInt(token[7].trim()));
				book.setCategory(token[8].trim());
				try {
					tx = session.beginTransaction();
					CompanyBean companyBean = 
							session.get(CompanyBean.class, Integer.parseInt(token[4].trim()));
					book.setCompanyBean(companyBean);
					
					session.save(book);
					tx.commit();
				} catch (Exception ex) {
					if (tx != null)
						tx.rollback();
				}
				System.out.println("新增一筆Book紀錄");
			}
			// 印出資料新增成功的訊息
			System.out.println("Book資料新增成功");
		} catch (Exception e) {
			System.err.println("新建BookCompany表格時發生IO例外: " + e.getMessage());
		}

		// 3. Member表格
		// 3-1 由"data/Input.txt"逐筆讀入Member表格內的初始資料，
		// 然後依序新增到Member表格中
		try (
			FileInputStream fis = new FileInputStream("data/Input.txt");
			InputStreamReader isr0 = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr0);
		) {
			while ((line = br.readLine()) != null) {
				String[] sa = line.split("\\|");
				MemberBean bean = new MemberBean();
				bean.setMemberId(sa[0]);
				bean.setName(sa[1]);
				String pswd = GlobalService.getMD5Endocing(GlobalService.encryptString(sa[2]));
				bean.setPassword(pswd);
				bean.setAddress(sa[3]);
				bean.setEmail(sa[4]);
				bean.setTel(sa[5]);
				bean.setUserType(sa[6]);
				bean.setTotalAmt(Double.parseDouble(sa[7]));
				bean.setTs(new java.sql.Timestamp(System.currentTimeMillis()));
				// --------------處理Blob(圖片)欄位----------------
				Blob sb = GlobalService.fileToBlob(sa[8]);
				bean.setMemberImage(sb);
				String imageFileName = sa[8].substring(sa[8].lastIndexOf("/") + 1);
				bean.setFileName(imageFileName);
				Clob clob = GlobalService.fileToClob(sa[9]);
				bean.setComment(clob);
				bean.setUnpaid_amount(0.0);

				try {
					tx = session.beginTransaction();
					session.save(bean);
					tx.commit();
				} catch (Exception ex) {
					if (tx != null)
						tx.rollback();
				}
				count++;
				System.out.println("新增" + count + "筆記錄:" + sa[1]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}