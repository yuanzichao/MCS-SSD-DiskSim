package org.tju.request;

import java.util.Timer;
import java.util.TimerTask;

import org.tju.bean.DiskInfo;
import org.tju.bean.FileInfo;
import org.tju.init.InitEnvironment;
import org.tju.init.InitEnvironmentPB;
import org.tju.statistics.DiskStateStat;

/**
 * Name: LightLoad
 * Description: 
 * 
 * @author yuan
 * @date 2014年10月19日 下午7:20:19
 */
public class Request {
	
	public static int totalRequestNum = 0;
	public InitEnvironment init;
	public String requests;
	
	
	public Request(){
		
	}
	
	public Request(InitEnvironment init, String request){
		this.init = init;
		this.requests = request;
		
	}
	
	//Light Lord
	public void request(InitEnvironment init, String fileName){
		
		if(init.getSSDDisk().getFilesList().get(fileName)!=null){
			totalRequestNum += 1;
			FileInfo fileInfo = init.getSSDDisk().getFilesList().get(fileName);
			System.out.println("Find It In SSD!!!");
			System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
			                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
					                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
					                                +fileInfo.getPriority()+"="+fileInfo.getSize());
			
			fileInfo.setRequestNum(fileInfo.getRequestNum()+1);
			init.getSSDDisk().getFilesList().put(fileName, fileInfo);
			
			ReplacementStrategy.calPriInCacheDisk(init.getSSDDisk(), totalRequestNum);
			init.getSSDDisk().getFilesList().put(fileName, fileInfo);
			
			return;
		}
		
		for(int i=0; i<4; i++){			
			if(init.getSecLevCache()[i].getFilesList().get(fileName)!=null){
				totalRequestNum += 1;
				FileInfo fileInfo = init.getSecLevCache()[i].getFilesList().get(fileName);
				System.out.println("Find It In HDD-"+i+"!!!");
				System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
				                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
						                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
						                                +fileInfo.getPriority()+"="+fileInfo.getSize());
				
				DiskStateStat.modifyDiskState(init.getSecLevCache()[i]);
				
				fileInfo.setRequestNum(fileInfo.getRequestNum()+1);
				init.getSecLevCache()[i].getFilesList().put(fileName, fileInfo);
				
				ReplacementStrategy.calPriInCacheDisk(init.getSecLevCache()[i], totalRequestNum);
				init.getSecLevCache()[i].getFilesList().put(fileName, fileInfo);
				
				return;
			}
		}
		
		String[] names = fileName.split("-");	
		int i = Integer.parseInt(names[0]);
		if(init.getDataDisks()[i].getFilesList().get(fileName)!=null){
			totalRequestNum += 1;
			FileInfo fileInfo = init.getDataDisks()[i].getFilesList().get(fileName);
			System.out.println("Find It In DataDisk-"+i+"!!!");
			System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
			                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
					                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
					                                +fileInfo.getPriority()+"="+fileInfo.getSize());
			
			DiskStateStat.modifyDiskState(init.getDataDisks()[i]);
			ReplacementStrategy.calPriInDataDisk(init.getDataDisks()[i], fileInfo);
			fileInfo.setIsHit(1);
			init.getDataDisks()[i].getFilesList().put(fileName, fileInfo);
			
			ReplacementStrategy.DDtoSSDReplacement(init.getSSDDisk(), init.getDataDisks()[i]);
			
			
			return;
		}
		
		
		System.out.println("Not Found!!!");
		
		
	}
	
	
    //Normal and Over Load
	public void request(InitEnvironment init, String fileName, int requestNum){
		
		if(init.getSSDDisk().getFilesList().get(fileName)!=null){
			
			totalRequestNum += 1;
			FileInfo fileInfo = init.getSSDDisk().getFilesList().get(fileName);
			System.out.println("Find It In SSD!!!");
			System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
			                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
					                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
					                                +fileInfo.getPriority()+"="+fileInfo.getSize());
			
			fileInfo.setRequestNum(fileInfo.getRequestNum()+requestNum+1);
			init.getSSDDisk().getFilesList().put(fileName, fileInfo);
			
			ReplacementStrategy.calPriInCacheDisk(init.getSSDDisk(), totalRequestNum);
			init.getSSDDisk().getFilesList().put(fileName, fileInfo);
			
			//refresh All
			ReplacementStrategy.refresh(init, requestNum, totalRequestNum);
			
			return;
		}
		
		for(int i=0; i<4; i++){			
			if(init.getSecLevCache()[i].getFilesList().get(fileName)!=null){
				
				totalRequestNum += 1;
				FileInfo fileInfo = init.getSecLevCache()[i].getFilesList().get(fileName);
				System.out.println("Find It In HDD-"+i+"!!!");
				System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
				                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
						                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
						                                +fileInfo.getPriority()+"="+fileInfo.getSize());
							
				
				fileInfo.setRequestNum(fileInfo.getRequestNum()+requestNum+1);
				init.getSecLevCache()[i].getFilesList().put(fileName, fileInfo);
				
				ReplacementStrategy.calPriInCacheDisk(init.getSecLevCache()[i], totalRequestNum);
				init.getSecLevCache()[i].getFilesList().put(fileName, fileInfo);
				
				//refresh All
				ReplacementStrategy.refresh(init, requestNum, totalRequestNum);
				
				return;
			}
		}
		
		String[] names = fileName.split("-");	
		int i = Integer.parseInt(names[0]);
		if(init.getDataDisks()[i].getFilesList().get(fileName)!=null){
			
			totalRequestNum += 1;
			FileInfo fileInfo = init.getDataDisks()[i].getFilesList().get(fileName);
			System.out.println("Find It In DataDisk-"+i+"!!!");
			System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
			                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
					                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
					                                +fileInfo.getPriority()+"="+fileInfo.getSize());
			
			DiskStateStat.modifyDiskState(init.getDataDisks()[i]);
			fileInfo.setRequestNum(fileInfo.getRequestNum()+requestNum+1);
			ReplacementStrategy.calPriInDataDisk(init.getDataDisks()[i], fileInfo);
			fileInfo.setIsHit(1);
			init.getDataDisks()[i].getFilesList().put(fileName, fileInfo);
			
			if(init.getSSDDisk().getLeftSpace()<=5000){
				for(int j=0; j<init.getSecLevCache().length; j++){
					if(init.getSecLevCache()[j].getLeftSpace()>1600){
						ReplacementStrategy.cacheReplacement(init.getSSDDisk(), init.getSecLevCache()[j], init.getDataDisks()[i]);
						break;				
					}
				}
			}else {
				ReplacementStrategy.DDtoSSDReplacement(init.getSSDDisk(), init.getDataDisks()[i]);
			}
			
			
			
			//refresh All
			ReplacementStrategy.refresh(init, requestNum, totalRequestNum);
			
			return;
		}
		
		
		System.out.println("Not Found!!!");
		
		
	}
	
	
	//Normal and Over Load for PB Group
	public void request(InitEnvironmentPB init, String fileName, int diskId, int requestNum){
		
		if(init.getSSDDisk().getFilesList().get(fileName)!=null){
			
			totalRequestNum += 1;
			FileInfo fileInfo = init.getSSDDisk().getFilesList().get(fileName);
			System.out.println("Find It In SSD!!!");
			System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
			                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
					                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
					                                +fileInfo.getPriority()+"="+fileInfo.getSize());
			
			fileInfo.setRequestNum(fileInfo.getRequestNum()+requestNum+1);
			init.getSSDDisk().getFilesList().put(fileName, fileInfo);
			
			ReplacementStrategy.calPriInCacheDisk(init.getSSDDisk(), totalRequestNum);
			init.getSSDDisk().getFilesList().put(fileName, fileInfo);
			
			//refresh All
			ReplacementStrategy.refresh(init, requestNum, totalRequestNum);
			
			return;
		}
		
		for(int i=0; i<4; i++){			
			if(init.getSecLevCache()[i].getFilesList().get(fileName)!=null){
				
				totalRequestNum += 1;
				FileInfo fileInfo = init.getSecLevCache()[i].getFilesList().get(fileName);
				System.out.println("Find It In HDD-"+i+"!!!");
				System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
				                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
						                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
						                                +fileInfo.getPriority()+"="+fileInfo.getSize());
							
				
				fileInfo.setRequestNum(fileInfo.getRequestNum()+requestNum+1);
				init.getSecLevCache()[i].getFilesList().put(fileName, fileInfo);
				
				ReplacementStrategy.calPriInCacheDisk(init.getSecLevCache()[i], totalRequestNum);
				init.getSecLevCache()[i].getFilesList().put(fileName, fileInfo);
				
				//refresh All
				ReplacementStrategy.refresh(init, requestNum, totalRequestNum);
				
				return;
			}
		}
		
		int i = diskId;
		
		DiskInfo diskFlag = init.getDataDisks()[i];
		
		if(init.getDataDisks()[i].getFilesList().get(fileName)!=null){
			
			totalRequestNum += 1;
			FileInfo fileInfo = init.getDataDisks()[i].getFilesList().get(fileName);
			System.out.println("Find It In DataDisk-"+i+"!!!");
			System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
			                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
					                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
					                                +fileInfo.getPriority()+"="+fileInfo.getSize());
			
			DiskStateStat.modifyDiskState(init.getDataDisks()[i]);
			fileInfo.setRequestNum(fileInfo.getRequestNum()+requestNum+1);
			ReplacementStrategy.calPriInDataDisk(init.getDataDisks()[i], fileInfo);
			fileInfo.setIsHit(1);
			init.getDataDisks()[i].getFilesList().put(fileName, fileInfo);
			
			if(init.getSSDDisk().getLeftSpace()<=5000){
				for(int j=0; j<init.getSecLevCache().length; j++){
					if(init.getSecLevCache()[j].getLeftSpace()>1600){
						ReplacementStrategy.cacheReplacement(init.getSSDDisk(), init.getSecLevCache()[j], init.getDataDisks()[i]);
						break;				
					}
				}
			}else {
				ReplacementStrategy.DDtoSSDReplacement(init.getSSDDisk(), init.getDataDisks()[i]);
			}
			
			
			
			//refresh All
			ReplacementStrategy.refresh(init, requestNum, totalRequestNum);
			
			return;
		}
		
		
		System.out.println("Not Found!!!");
		
		
	}
	
	
	//Light Lord
	public void requestMAID(InitEnvironment init, String fileName){
		
		if(init.getSSDDisk().getFilesList().get(fileName)!=null){
			totalRequestNum += 1;
			FileInfo fileInfo = init.getSSDDisk().getFilesList().get(fileName);
			System.out.println("Find It In SSD!!!");
			System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
			                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
					                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
					                                +fileInfo.getPriority()+"="+fileInfo.getSize());
			
			fileInfo.setRequestNum(fileInfo.getRequestNum()+1);
			init.getSSDDisk().getFilesList().put(fileName, fileInfo);
			
			ReplacementStrategy.calPriInCacheDisk(init.getSSDDisk(), totalRequestNum);
			init.getSSDDisk().getFilesList().put(fileName, fileInfo);
			
			return;
		}
		
//		for(int i=0; i<4; i++){			
//			if(init.getSecLevCache()[i].getFilesList().get(fileName)!=null){
//				totalRequestNum += 1;
//				FileInfo fileInfo = init.getSecLevCache()[i].getFilesList().get(fileName);
//				System.out.println("Find It In HDD-"+i+"!!!");
//				System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
//				                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
//						                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
//						                                +fileInfo.getPriority()+"="+fileInfo.getSize());
//				
//				DiskStateStat.modifyDiskState(init.getSecLevCache()[i]);
//				
//				fileInfo.setRequestNum(fileInfo.getRequestNum()+1);
//				init.getSecLevCache()[i].getFilesList().put(fileName, fileInfo);
//				
//				ReplacementStrategy.calPriInCacheDisk(init.getSecLevCache()[i], totalRequestNum);
//				init.getSecLevCache()[i].getFilesList().put(fileName, fileInfo);
//				
//				return;
//			}
//		}
		
		String[] names = fileName.split("-");	
		int i = Integer.parseInt(names[0]);
		if(init.getDataDisks()[i].getFilesList().get(fileName)!=null){
			totalRequestNum += 1;
			FileInfo fileInfo = init.getDataDisks()[i].getFilesList().get(fileName);
			System.out.println("Find It In DataDisk-"+i+"!!!");
			System.out.println("File's Information:"+fileInfo.getFileId()+"="+fileInfo.getFileName()+"="
			                                        +fileInfo.getDiskInId()+"="+fileInfo.getSkyZone()+"="
					                                +fileInfo.getObserveTime()+"="+fileInfo.getRequestNum()+"="
					                                +fileInfo.getPriority()+"="+fileInfo.getSize());
			
			DiskStateStat.modifyDiskState(init.getDataDisks()[i]);
//			ReplacementStrategy.calPriInDataDisk(init.getDataDisks()[i], fileInfo);
			fileInfo.setIsHit(1);
			init.getDataDisks()[i].getFilesList().put(fileName, fileInfo);
			
//			ReplacementStrategy.DDtoSSDReplacement(init.getSSDDisk(), init.getDataDisks()[i]);
			
			
			return;
		}
		
		
		System.out.println("Not Found!!!");
		
		
	}	
	
	/**
	 * @return the totalRequestNum
	 */
	public int getTotalRequestNum() {
		return totalRequestNum;
	}


	/**
	 * @param totalRequestNum the totalRequestNum to set
	 */
	@SuppressWarnings("static-access")
	public void setTotalRequestNum(int totalRequestNum) {
		this.totalRequestNum = totalRequestNum;
	}


	/**
	 * @return the init
	 */
	public InitEnvironment getInit() {
		return init;
	}


	/**
	 * @param init the init to set
	 */
	public void setInit(InitEnvironment init) {
		this.init = init;
	}


	/**
	 * @return the request
	 */
	public String getRequest() {
		return requests;
	}


	/**
	 * @param request the request to set
	 */
	public void setRequest(String request) {
		this.requests = request;
	}
	
	
//	/* (non-Javadoc)
//	 * @see java.lang.Runnable#run()
//	 */
//	@Override
//	public void run() {
//		request(init, requests);	
//	}


	/**
	 * Name: main
	 * Description: 
	 * @param args
	 *
	 * @author yuan
	 * @date 2014年10月19日 下午7:20:19
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InitEnvironment init = new InitEnvironment();
		init.initEnvironment();
		Request lightLoad = new Request();
//		lightLoad.request(init, "14-598-133");
//		lightLoad.request(init, "14-598-133");
//		lightLoad.request(init, "14-597-133");
//		lightLoad.request(init, "14-598-134");
//		lightLoad.request(init, "14-595-134");
//		lightLoad.request(init, "14-594-134");
		
		final Timer timer = new Timer();

        TimerTask tt=new TimerTask() { 
            @Override
            public void run() {
            	lightLoad.request(init, "14-598-133");
            }
        };
        
        TimerTask tt1=new TimerTask() { 
            @Override
            public void run() {
            	lightLoad.request(init, "14-597-133");
            }
        };

        timer.schedule(tt, 3000);
        
        timer.schedule(tt1, 6000);
	}
}
