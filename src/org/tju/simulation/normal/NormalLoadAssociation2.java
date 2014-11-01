package org.tju.simulation.normal;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
//import java.util.TimerTask;





import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.tju.init.InitEnvironment;
import org.tju.request.Request;
import org.tju.statistics.DiskStateStat;
import org.tju.tool.ReadXml;

/**
 * @author yuan
 *
 * @date 2014年10月23日 下午2:06:21
 */
public class NormalLoadAssociation2 {
	
	//Refresh Time
	public static final int refreshTime = Integer.parseInt(ReadXml.readname("config/RefreshTime.xml", "refreshtime"));

	/**
	 * Name: main
	 * Description: 
	 * @param args
	 *
	 * @author yuan
	 * @date 2014年10月20日 下午4:06:21
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InitEnvironment init = new InitEnvironment();
		init.initEnvironment();
		
		Request normalLord = new Request();
		
		List<String> requestList = new LinkedList<String>();
		
		Random rd = new Random();
		for(int i=0; i<2000; i++){
			int diskId = rd.nextInt(14);
			int skyZoneId = diskId*40 + rd.nextInt(40);
			int observeTime = rd.nextInt(150);
			String requestFileName = String.valueOf(diskId) + "-" + 
			                         String.valueOf(skyZoneId) + "-" + 
					                 String.valueOf(observeTime);			
			requestList.add(requestFileName);
			
//					
//			requestFileName = String.valueOf(diskId) + "-" + 
//                              String.valueOf(skyZoneId-rd.nextInt(3)) + "-" + 
//                              String.valueOf(observeTime-rd.nextInt(3));
//            requestList.add(requestFileName);
//            
//            requestFileName = String.valueOf(diskId) + "-" + 
//                              String.valueOf(skyZoneId+rd.nextInt(3)) + "-" + 
//                              String.valueOf(observeTime+rd.nextInt(3));
//            requestList.add(requestFileName);
//            
//            requestFileName = String.valueOf(diskId) + "-" + 
//                              String.valueOf(skyZoneId-rd.nextInt(3)) + "-" + 
//                              String.valueOf(observeTime+rd.nextInt(3));
//            requestList.add(requestFileName);
//            
//            requestFileName = String.valueOf(diskId) + "-" + 
//                              String.valueOf(skyZoneId+rd.nextInt(3)) + "-" + 
//                              String.valueOf(observeTime-rd.nextInt(3));
//            requestList.add(requestFileName);
				
			
			
			requestFileName = String.valueOf(diskId) + "-" + 
                              String.valueOf(skyZoneId-1) + "-" + 
	                          String.valueOf(observeTime);
			requestList.add(requestFileName);
			
			requestFileName = String.valueOf(diskId) + "-" + 
                              String.valueOf(skyZoneId+1) + "-" + 
                              String.valueOf(observeTime);
			requestList.add(requestFileName);
			
			requestFileName = String.valueOf(diskId) + "-" + 
                              String.valueOf(skyZoneId) + "-" + 
                              String.valueOf(observeTime-1);
			requestList.add(requestFileName);
			
			requestFileName = String.valueOf(diskId) + "-" + 
                              String.valueOf(skyZoneId) + "-" + 
                              String.valueOf(observeTime+1);
            requestList.add(requestFileName);

//            
//            requestFileName = String.valueOf(diskId) + "-" + 
//                    String.valueOf(skyZoneId-1) + "-" + 
//                    String.valueOf(observeTime);
//	        requestList.add(requestFileName);            
//            requestFileName = String.valueOf(diskId) + "-" + 
//                    String.valueOf(skyZoneId-1) + "-" + 
//                    String.valueOf(observeTime);
//	        requestList.add(requestFileName);            
//            requestFileName = String.valueOf(diskId) + "-" + 
//                    String.valueOf(skyZoneId-1) + "-" + 
//                    String.valueOf(observeTime);
//	        requestList.add(requestFileName);            
//            requestFileName = String.valueOf(diskId) + "-" + 
//                    String.valueOf(skyZoneId-1) + "-" + 
//                    String.valueOf(observeTime);
//	        requestList.add(requestFileName);            
//            requestFileName = String.valueOf(diskId) + "-" + 
//                    String.valueOf(skyZoneId-1) + "-" + 
//                    String.valueOf(observeTime);
//	        requestList.add(requestFileName);			
		}
		
		//Upset The Order of The List
		Collections.shuffle(requestList);
		
		
		
		try { 
			//打开文件 
		    WritableWorkbook book= Workbook.createWorkbook(new File("outputData/outputData.xls")); 
		    //生成名为“第一页”的工作表，参数0表示这是第一页 
		    WritableSheet sheet=book.createSheet("第一页",0); 
		    //在Label对象的构造子中指名单元格位置是第一列第一行(0,0) 
		    Label label=new Label(0,0,"Time"); 
		    sheet.addCell(label); 
		    
		    //First Row
		    for(int i=0; i<init.getDataDisks().length; i++){
		    	label = new Label(i+1, 0, String.valueOf(i));
		    	sheet.addCell(label);	    	
		    }
		    
		    label = new Label(init.getDataDisks().length+1, 0, "HDDOpenNum");
		    sheet.addCell(label);
		    
		    label = new Label(init.getDataDisks().length+2, 0, "OpenDisks");
		    sheet.addCell(label);
		    
		    label = new Label(init.getDataDisks().length+3, 0, "ClosedDisks");
		    sheet.addCell(label);
		    
//		    //First Column
//		    for(int i=0; i<requestList.size(); i++){
//		    	label = new Label(0, i+1, String.valueOf(i));
//		    	sheet.addCell(label);
//		    }
		    
		    for(int i=0; i<requestList.size(); i++){
				
				String requestFileName = requestList.get(i);
				
				normalLord.request(init, requestFileName, i%refreshTime);
				
				DiskStateStat.idleTimeStat(init.getDataDisks());
				
				DiskStateStat.closeDisk(init.getDataDisks());
				
				jxl.write.Number index = new jxl.write.Number(0, i+1, i);
				sheet.addCell(index); 

				
				for(int j=0; j<init.getDataDisks().length; j++){
					jxl.write.Number number = new jxl.write.Number(j+1, i+1, init.getDataDisks()[j].getDiskState());
					sheet.addCell(number); 
				}
				
				jxl.write.Number HDDOpenNum = new jxl.write.Number(init.getDataDisks().length+1, i+1, DiskStateStat.diskStateStat(init.getSecLevCache()));
				sheet.addCell(HDDOpenNum);
				
				jxl.write.Number openDisksNum = new jxl.write.Number(init.getDataDisks().length+2, i+1, DiskStateStat.diskStateStat(init.getDataDisks()));
				sheet.addCell(openDisksNum);
				
				jxl.write.Number closedDisksNum = new jxl.write.Number(init.getDataDisks().length+3, i+1, 15-DiskStateStat.diskStateStat(init.getDataDisks()));
				sheet.addCell(closedDisksNum);
			}
		
		    //写入数据并关闭文件 
		    book.write(); 
		    book.close(); //最好在finally中关闭，此处仅作为示例不太规范
	    }catch(Exception e) { 
	        System.out.println(e); 
	    } 

	}

}
