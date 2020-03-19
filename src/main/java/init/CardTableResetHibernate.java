package init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Clob;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import _03_listBooks.model.CompanyBean;
import card.CardsBean;
import member.model.MemberBean;

// 本程式建立CARDS的表格
 
public class CardTableResetHibernate {
//	public static final String UTF8_BOM = "\uFEFF"; // 定義 UTF-8的BOM字元

	public static void main(String args[]) {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.openSession();
		Transaction tx = null;

		String line = "";
		int count = 0;
		// 1. BookCompany表格
		// 1-1 由"data/bookCompany.dat"逐筆讀入BookCompany表格內的初始資料，
		// 然後依序新增到BookCompany表格中
//		try (
//			FileReader fr = new FileReader("data/cardcsv.csv"); 
//			BufferedReader br = new BufferedReader(fr);
//		) {
//			while ((line = br.readLine()) != null) {
//				if (line.startsWith(UTF8_BOM)) {
//					line = line.substring(1);
//				}
//				String[] token = line.split("\\|");
//				String name = token[0];
//				String address = token[1];
//				String url = token[2];
//				CompanyBean cb = new CompanyBean(null, name, address, url);
//				try {
//					tx = session.beginTransaction();
//					session.save(cb);
//					tx.commit();
//				} catch (Exception ex) {
//					if (tx != null)
//						tx.rollback();
//				}
//			}
//			System.out.println("BookCompany 資料新增成功");
//		} catch (IOException e) {
//			System.err.println("新建BookCompany表格時發生IO例外: " + e.getMessage());
//		}

		// 2. Book表格
		File file = new File("data/cardcsv.csv");
		
		try (
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "BIG5");
			BufferedReader br = new BufferedReader(isr);
		) { int brcnt ;
		        brcnt = 0;
			while ((line = br.readLine()) != null) {
				 brcnt++;
					if (count < 2) {
						continue;
					}
					System.out.println("line=" + line);
					
					String[] token = line.split(",");
				

				CardsBean cb = new CardsBean();
//				cb.setC_id();
				cb.setC_name(token[1]);
				cb.setBank(token[2]);
				cb.setAnnlfee(token[3]);
				cb.setC_img(blob);(Integer.parseInt(token[4].trim()));
				cb.setFcb(Double.parseDouble(token[5].trim()));
				cb.setDcb(Double.parseDouble(token[6].trim()));
				cb.setFma(token[7]) 	;		
				cb.setDma(token[8]) 	;		
				cb.setFma_n(token[9]) 	;		
				cb.setDma_n(token[10]) 	;		
				cb.setRefuel_cb(Double.parseDouble(token[11].trim())) ;		
				cb.setRefuel_dis(token[12]) 	;	
				cb.setRefuel_cb_n(token[13]) ;	
				cb.setRefuel_dis_n(token[14]) ;
				cb.setWkdmoviedis(token[15]) 	;
				cb.setMoviedis(token[16]) 		;
				cb.setWkdmoviedis_n(token[17]) ;
				cb.setMoviedis_n(token[18]) 		;
				cb.setDitigalfb(token[19]) 		;
				cb.setOnlinefb(token[20]) 		;
				cb.setDitigalfb_n(token[21]) 	;
				cb.setOnlinefb_n(token[22]) 		;
				cb.setZeroins(token[23])		;
				cb.setMinins(token[24])			;
				cb.setZeroins_n(token[25])		;
				cb.setMinins_n(token[26])		;
				cb.setMileagefb(token[27])		;
				cb.setCashfb(token[28]) 			;
				cb.setMileagefb_n(token[29]) 	;
				cb.setCashfb_n(token[30]) 		;
				cb.setAnnual_income(Integer.parseInt(token[31].trim())) ;
				cb.setC_type(token[32]) 		  ;
				
				try {
					tx = session.beginTransaction();
				//	設外鍵
//					CompanyBean companyBean = 
//							session.get(CompanyBean.class, Integer.parseInt(token[4].trim()));
//					book.setCompanyBean(companyBean);
					
					session.save(cb);
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