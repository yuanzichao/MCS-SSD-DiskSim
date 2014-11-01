package org.tju.simulation.PBGroup;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.tju.init.InitEnvironmentPB;
import org.tju.request.Request;
import org.tju.statistics.DiskStateStat;
import org.tju.tool.ReadXml;

/**
 * @author yuan
 *
 * @date 2014年10月23日 下午2:06:21
 */
public class NormalLoadPBGroup {
	
	//Group NUmber
	public static int groupNum = 40;
	
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
		//init environment of PB Group
		InitEnvironmentPB[] init = new InitEnvironmentPB[groupNum];
		for(int i=0; i<groupNum; i++){
			init[i] = new InitEnvironmentPB();
			init[i].initEnvironment(i);
		}

		
		Request normalLord = new Request();
		
		List<LinkedList<RequestsKeyAndValue>> requestList = new LinkedList<LinkedList<RequestsKeyAndValue>>();
		for(int i=0; i<groupNum; i++){
			LinkedList<RequestsKeyAndValue> requestNameList =  new LinkedList<RequestsKeyAndValue>();
			requestList.add(requestNameList);
		}
		
		Random rd = new Random();
		for(int i=0; i<80000; i++){
			int groupId = rd.nextInt(groupNum);
			int diskId = groupId * 15 + rd.nextInt(15);
			int skyZoneId = diskId*13 + rd.nextInt(13);
			int observeTime = rd.nextInt(600);
			String requestFileName = String.valueOf(diskId) + "-" + 
			                         String.valueOf(skyZoneId) + "-" + 
					                 String.valueOf(observeTime);
			
			RequestsKeyAndValue requestKV = new RequestsKeyAndValue(diskId-groupId*15, requestFileName);
			
			requestList.get(groupId).add(requestKV);
	
			requestFileName = String.valueOf(diskId) + "-" + 
                              String.valueOf(skyZoneId-1) + "-" + 
	                          String.valueOf(observeTime);
			requestKV = new RequestsKeyAndValue(diskId-groupId*15, requestFileName);
			requestList.get(groupId).add(requestKV);
			
			requestFileName = String.valueOf(diskId) + "-" + 
                              String.valueOf(skyZoneId+1) + "-" + 
                              String.valueOf(observeTime);
			requestKV = new RequestsKeyAndValue(diskId-groupId*15, requestFileName);
			requestList.get(groupId).add(requestKV);
			
			requestFileName = String.valueOf(diskId) + "-" + 
                              String.valueOf(skyZoneId) + "-" + 
                              String.valueOf(observeTime-1);
			requestKV = new RequestsKeyAndValue(diskId-groupId*15, requestFileName);
			requestList.get(groupId).add(requestKV);
			
			requestFileName = String.valueOf(diskId) + "-" + 
                              String.valueOf(skyZoneId) + "-" + 
                              String.valueOf(observeTime+1);
			requestKV = new RequestsKeyAndValue(diskId-groupId*15, requestFileName);
			requestList.get(groupId).add(requestKV);
			
		}
		
		//Upset The Order of The List
		for(int i=0; i<groupNum; i++){
			Collections.shuffle(requestList.get(i));
		}
				
		try {
			for(int i=0; i<groupNum; i++){
				String outputDataName = "outputData/outputData" + i + ".xls";
				//Open file
			    WritableWorkbook book= Workbook.createWorkbook(new File(outputDataName)); 
			    //Generate the first sheet
			    WritableSheet sheet=book.createSheet("First Sheet",0); 
			    //Put The Label in(0,0) 
			    Label label=new Label(0,0,"Time"); 
			    sheet.addCell(label); 
			    
			    //First Row
			    for(int j=0; j<init[i].getDataDisks().length; j++){
			    	label = new Label(j+1, 0, String.valueOf(j));
			    	sheet.addCell(label);	    	
			    }
			    
			    label = new Label(init[i].getDataDisks().length+1, 0, "HDDOpenNum");
			    sheet.addCell(label);
			    
			    label = new Label(init[i].getDataDisks().length+2, 0, "OpenDisks");
			    sheet.addCell(label);
			    
			    label = new Label(init[i].getDataDisks().length+3, 0, "ClosedDisks");
			    sheet.addCell(label);
			    
			    for(int j=0; j<requestList.get(i).size(); j++){
					
					String requestFileName = requestList.get(i).get(j).getFileName();
					int diskId = requestList.get(i).get(j).getDiskId();
					
					normalLord.request(init[i], requestFileName, diskId, i%refreshTime);
					
					DiskStateStat.idleTimeStat(init[i].getDataDisks());
					
					DiskStateStat.closeDisk(init[i].getDataDisks());
					
					jxl.write.Number index = new jxl.write.Number(0, j+1, j);
					sheet.addCell(index); 

					
					for(int k=0; k<init[i].getDataDisks().length; k++){
						jxl.write.Number number = new jxl.write.Number(k+1, j+1, init[i].getDataDisks()[k].getDiskState());
						sheet.addCell(number); 
					}
					
					jxl.write.Number HDDOpenNum = new jxl.write.Number(init[i].getDataDisks().length+1, j+1, DiskStateStat.diskStateStat(init[i].getSecLevCache()));
					sheet.addCell(HDDOpenNum);
					
					jxl.write.Number openDisksNum = new jxl.write.Number(init[i].getDataDisks().length+2, j+1, DiskStateStat.diskStateStat(init[i].getDataDisks()));
					sheet.addCell(openDisksNum);
					
					jxl.write.Number closedDisksNum = new jxl.write.Number(init[i].getDataDisks().length+3, j+1, 15-DiskStateStat.diskStateStat(init[i].getDataDisks()));
					sheet.addCell(closedDisksNum);
				}
			
			    //Write data to the file
			    book.write(); 
			    book.close();
			}
			
	    }catch(Exception e) { 
	        System.out.println(e); 
	    } 

	}

}
