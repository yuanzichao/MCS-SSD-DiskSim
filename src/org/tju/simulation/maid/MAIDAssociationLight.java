package org.tju.simulation.maid;

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
 * @date 2014年10月27日 下午3:06:21
 */
public class MAIDAssociationLight {
	
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
		
		Request lightLoad = new Request();
		
		List<String> requestList = new LinkedList<String>();
		
		Random rd = new Random();
		for(int i=0; i<1000; i++){
			int diskId = rd.nextInt(14);
			int skyZoneId = diskId*40 + rd.nextInt(40);
			int observeTime = rd.nextInt(150);
			String requestFileName = String.valueOf(diskId) + "-" + 
			                         String.valueOf(skyZoneId) + "-" + 
					                 String.valueOf(observeTime);			
			requestList.add(requestFileName);
			
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
			
		}
		
		//Upset The Order of The List
		Collections.shuffle(requestList);
		
		
		
		try { 
			//Open File
		    WritableWorkbook book= Workbook.createWorkbook(new File("outputData/outputData.xls")); 
		    //Generate the first sheet
		    WritableSheet sheet=book.createSheet("First Sheet",0); 
		    //Put The Label in(0,0) 
		    Label label=new Label(0,0,"Time"); 
		    sheet.addCell(label); 
		    
		    //First Row
		    for(int i=0; i<init.getDataDisks().length; i++){
		    	label = new Label(i+1, 0, String.valueOf(i));
		    	sheet.addCell(label);	    	
		    }
		    
		    label = new Label(init.getDataDisks().length+1, 0, "OpenDisks");
		    sheet.addCell(label);
		    
		    label = new Label(init.getDataDisks().length+2, 0, "ClosedDisks");
		    sheet.addCell(label);
		    
		    //First Column
		    for(int i=0; i<requestList.size(); i++){
		    	label = new Label(0, i+1, String.valueOf(i));
		    	sheet.addCell(label);
		    }
		    
		    for(int i=0; i<requestList.size(); i++){
				
				String requestFileName = requestList.get(i);
				
				lightLoad.requestMAID(init, requestFileName, i%refreshTime);
				
				DiskStateStat.idleTimeStat(init.getDataDisks());
				
				DiskStateStat.closeDisk(init.getDataDisks());
				
				jxl.write.Number index = new jxl.write.Number(0, i+1, i);
				sheet.addCell(index); 

				
				for(int j=0; j<init.getDataDisks().length; j++){
					jxl.write.Number number = new jxl.write.Number(j+1, i+1, init.getDataDisks()[j].getDiskState());
					sheet.addCell(number); 
				}
				
				jxl.write.Number openDisksNum = new jxl.write.Number(init.getDataDisks().length+1, i+1, DiskStateStat.diskStateStat(init.getDataDisks()));
				sheet.addCell(openDisksNum);
				
				jxl.write.Number closedDisksNum = new jxl.write.Number(init.getDataDisks().length+2, i+1, 15-DiskStateStat.diskStateStat(init.getDataDisks()));
				sheet.addCell(closedDisksNum);
			}
		
		    //Write data to the file
		    book.write(); 
		    book.close();
	    }catch(Exception e) { 
	        System.out.println(e); 
	    } 

	}

}
