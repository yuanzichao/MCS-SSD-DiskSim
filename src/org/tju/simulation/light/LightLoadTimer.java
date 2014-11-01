package org.tju.simulation.light;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.tju.init.InitEnvironment;
import org.tju.request.Request;
import org.tju.statistics.DiskStateStat;

/**
 * @author yuan
 *
 * @date 2014��10��20�� ����4:06:21
 */
public class LightLoadTimer {

	/**
	 * Name: main
	 * Description: 
	 * @param args
	 *
	 * @author yuan
	 * @date 2014��10��20�� ����4:06:21
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InitEnvironment init = new InitEnvironment();
		init.initEnvironment();
		
		Request lightLoad = new Request();
		
		List<String> requestList = new LinkedList<String>();
		List<TimerTask> requestTimeList = new LinkedList<TimerTask>();
		
		
		final Timer timer = new Timer();
		
		Random rd = new Random();
		for(int i=0; i<5; i++){
			int diskId = rd.nextInt(14);
			int skyZoneId = diskId*40 + rd.nextInt(40);
			int observeTime = rd.nextInt(150);
			String requestFileName = String.valueOf(diskId) + "-" + 
			                         String.valueOf(skyZoneId) + "-" + String.valueOf(observeTime);
			
			requestList.add(requestFileName);
		}
		
		for(int i=0; i<requestList.size(); i++){
			
			String requestFileName = requestList.get(i);
			
			TimerTask tt = new TimerTask() {
				@Override
				public void run() {
					lightLoad.request(init, requestFileName);
				}
			};
			requestTimeList.add(tt);
		  
		}
//		
//		for(int i=0; i<requestTimeList.size(); i++){
//			timer.schedule(requestTimeList.get(i), 100+100*i);
//		}
		
		try { 
			//���ļ� 
		    WritableWorkbook book= Workbook.createWorkbook(new File("outputData/outputData.xls")); 
		    //������Ϊ����һҳ���Ĺ���������0��ʾ���ǵ�һҳ 
		    WritableSheet sheet=book.createSheet("��һҳ",0); 
		    //��Label����Ĺ�������ָ����Ԫ��λ���ǵ�һ�е�һ��(0,0) 
		    Label label=new Label(0,0,"Time"); 
		    sheet.addCell(label); 
		    
		    //First Row
		    for(int i=0; i<init.getDataDisks().length; i++){
		    	label = new Label(i+1, 0, String.valueOf(i));
		    	sheet.addCell(label);	    	
		    }
		    
		    label = new Label(init.getDataDisks().length+1, 0, "OpenDisks");
		    sheet.addCell(label);
		    
		    //First Column
		    for(int i=0; i<requestTimeList.size(); i++){
		    	label = new Label(0, i+1, String.valueOf(i));
		    	sheet.addCell(label);
		    }
		    
			for(int i=0; i<requestTimeList.size(); i++){
				
				timer.schedule(requestTimeList.get(i), 10000*i);
				
				DiskStateStat.idleTimeStat(init.getDataDisks());
				
				DiskStateStat.closeDisk(init.getDataDisks());
				
				for(int j=0; j<init.getDataDisks().length; j++){
					jxl.write.Number number = new jxl.write.Number(j+1, i+1, init.getDataDisks()[j].getDiskState());
					sheet.addCell(number); 
				}
				
				jxl.write.Number openDisksNum = new jxl.write.Number(init.getDataDisks().length+1, i+1, DiskStateStat.diskStateStat(init.getDataDisks()));
				sheet.addCell(openDisksNum);
			}
		
		    //д�����ݲ��ر��ļ� 
		    book.write(); 
		    book.close(); //�����finally�йرգ��˴�����Ϊʾ����̫�淶
	    }catch(Exception e) { 
	        System.out.println(e); 
	    } 

	}

}
