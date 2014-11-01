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
public class NormalLoadPBGroup1 {
	
	//Group NUmber
	public static int groupNum = 2;
	
	//Refresh Time
	public static final int refreshTime = Integer.parseInt(ReadXml.readname("config/RefreshTime.xml", "refreshtime"));

	
	//Requests Group
	public static void requestGroup(){
		
	}
	
	
	
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
//		InitEnvironmentPB[] init = new InitEnvironmentPB[groupNum];
//		for(int i=0; i<groupNum; i++){
//			init[i] = new InitEnvironmentPB();
//			init[i].initEnvironment(i);
//		}
		
		List<InitEnvironmentPB> init = new LinkedList<InitEnvironmentPB>();
		for(int i=0; i<groupNum; i++){
			init.add(new InitEnvironmentPB());
			init.get(i).initEnvironment(i);
		}

		
		Request normalLord = new Request();
		
		List<LinkedList<RequestsKeyAndValue>> requestList = new LinkedList<LinkedList<RequestsKeyAndValue>>();
		for(int i=0; i<groupNum; i++){
			LinkedList<RequestsKeyAndValue> requestNameList =  new LinkedList<RequestsKeyAndValue>();
			requestList.add(requestNameList);
		}
		
		Random rd = new Random();
		for(int i=0; i<1; i++){
			int groupId = rd.nextInt(groupNum-1);
			int diskId = groupId * 15 + rd.nextInt(14);
			int skyZoneId = diskId*13 + rd.nextInt(12);
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
				//打开文件 
			    WritableWorkbook book= Workbook.createWorkbook(new File(outputDataName)); 
			    //生成名为“第一页”的工作表，参数0表示这是第一页 
			    WritableSheet sheet=book.createSheet("第一页",0); 
			    //在Label对象的构造子中指名单元格位置是第一列第一行(0,0) 
			    Label label=new Label(0,0,"Time"); 
			    sheet.addCell(label); 
			    
			    //First Row
			    for(int j=0; j<init.get(i).getDataDisks().length; j++){
			    	label = new Label(j+1, 0, String.valueOf(j));
			    	sheet.addCell(label);	    	
			    }
			    
			    label = new Label(init.get(i).getDataDisks().length+1, 0, "HDDOpenNum");
			    sheet.addCell(label);
			    
			    label = new Label(init.get(i).getDataDisks().length+2, 0, "OpenDisks");
			    sheet.addCell(label);
			    
			    label = new Label(init.get(i).getDataDisks().length+3, 0, "ClosedDisks");
			    sheet.addCell(label);
			    
			    //First Column
			    for(int j=0; j<requestList.get(i).size(); j++){
			    	label = new Label(0, j+1, String.valueOf(j));
			    	sheet.addCell(label);
			    }
			    
			    for(int j=0; j<requestList.get(i).size(); j++){
					
					String requestFileName = requestList.get(i).get(j).getFileName();
					int diskId = requestList.get(i).get(j).getDiskId();
					
					normalLord.request(init.get(i), requestFileName, diskId, i%refreshTime);
					
					DiskStateStat.idleTimeStat(init.get(i).getDataDisks());
					
					DiskStateStat.closeDisk(init.get(i).getDataDisks());
					
					jxl.write.Number index = new jxl.write.Number(0, i+1, i);
					sheet.addCell(index); 

					
					for(int k=0; k<init.get(i).getDataDisks().length; k++){
						jxl.write.Number number = new jxl.write.Number(k+1, k+1, init.get(i).getDataDisks()[k].getDiskState());
						sheet.addCell(number); 
					}
					
					jxl.write.Number HDDOpenNum = new jxl.write.Number(init.get(i).getDataDisks().length+1, i+1, DiskStateStat.diskStateStat(init.get(i).getSecLevCache()));
					sheet.addCell(HDDOpenNum);
					
					jxl.write.Number openDisksNum = new jxl.write.Number(init.get(i).getDataDisks().length+2, i+1, DiskStateStat.diskStateStat(init.get(i).getDataDisks()));
					sheet.addCell(openDisksNum);
					
					jxl.write.Number closedDisksNum = new jxl.write.Number(init.get(i).getDataDisks().length+3, i+1, 15-DiskStateStat.diskStateStat(init.get(i).getDataDisks()));
					sheet.addCell(closedDisksNum);
				}
			
			    //写入数据并关闭文件 
			    book.write(); 
			    book.close(); //最好在finally中关闭，此处仅作为示例不太规范
			}
			
	    }catch(Exception e) { 
	        System.out.println(e); 
	    } 

	}

}
