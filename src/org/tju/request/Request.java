package org.tju.request;

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
	public void requestLight(InitEnvironment init, String fileName, int requestNum){
		
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
			
			//refresh All
			ReplacementStrategy.refresh(init, requestNum, totalRequestNum);
			
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
	
	
	//Light Lord of MAID
	public void requestMAID(InitEnvironment init, String fileName, int requestNum){
		
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
			fileInfo.setIsHit(1);
			init.getDataDisks()[i].getFilesList().put(fileName, fileInfo);
			
			if(init.getSSDDisk().getLeftSpace()>=200){
				ReplacementStrategy.MAIDReplacement(init.getSSDDisk(), init.getDataDisks()[i]);
			}else {
				
				for(int j=0; j<init.getSecLevCache().length; j++){
					if(init.getSecLevCache()[j].getLeftSpace()>=200){
						init.getSecLevCache()[j].setDiskState(1);
						ReplacementStrategy.MAIDCacheReplacement(init.getSecLevCache()[j], init.getDataDisks()[i]);
						break;				
					}
				}	
			}
						
			//refresh All
			ReplacementStrategy.refresh(init, requestNum, totalRequestNum);
			
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
		
}
