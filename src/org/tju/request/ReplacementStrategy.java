package org.tju.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;







import org.tju.bean.DiskInfo;
import org.tju.bean.FileInfo;
import org.tju.init.InitEnvironment;
import org.tju.init.InitEnvironmentPB;
import org.tju.statistics.DiskStateStat;
import org.tju.tool.ReadXml;

/**
 * Name: ReplacementStrategy
 * Description: Replacement Strategy of Disks
 * 
 * @author yuan
 * @date 2014年10月20日 上午9:44:32
 */
public class ReplacementStrategy {
	
	private static double lightThreshold = 0.3;       //The Light Priority Threshold
	private static double highThreshold = 2;          //The Most Priority Threshold
	private static double normalThreshold = 0.5;      //The Normal Priority Threshold
	
	
	//Sorted By Priority
	public static void sortedByPriority(HashMap<String, FileInfo> fileMap){
		List<Entry<String, FileInfo>> list = new ArrayList<Entry<String,FileInfo>>(fileMap.entrySet());
				
		Collections.sort(list, new Comparator<Map.Entry<String, FileInfo>>(){
			                       public int compare(Map.Entry<String, FileInfo> e1, Map.Entry<String, FileInfo> e2){         	   
			                    	   double v1 = (double) ((Map.Entry<String, FileInfo>)e1).getValue().getPriority(); 
			                    	   double v2 = (double) ((Map.Entry<String, FileInfo>)e2).getValue().getPriority(); 
			                    	   double flag = v2-v1;
			                    	   if(flag > 0.0){
			                    		   return 1;
			                    	   }else if(flag == 0){
										return 0;
									   }else {
										return -1;
									}
			                         }
			                       }); 
		
		fileMap.clear();
		
		for(int i=0; i<list.size(); i++){
			fileMap.put(list.get(i).getKey(), list.get(i).getValue());
		}
	}
	
	
	
	//Priority Calculation
	//Data Disks' files
	public static void calPriInDataDisk(DiskInfo disk, FileInfo hitFile){
		Iterator<Entry<String, FileInfo>> iter = disk.getFilesList().entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, FileInfo> entry = iter.next();
			FileInfo file = entry.getValue();
			
			if(file.getIsHit()!=1){
				if(file.getObserveTime()!=hitFile.getObserveTime()){
					if(file.getSkyZone()!=hitFile.getSkyZone()){
						file.setPriority(Math.abs(1.0/((file.getObserveTime()-hitFile.getObserveTime())*(file.getSkyZone()-hitFile.getSkyZone()))));
					}else {
						file.setPriority(Math.abs(2.0/(file.getObserveTime()-hitFile.getObserveTime())));
					}
				}else{
					if(file.getSkyZone()!=hitFile.getSkyZone()){
						file.setPriority(Math.abs(2.0/(file.getSkyZone()-hitFile.getSkyZone())));
					}
				}
			}
			
			disk.getFilesList().put(entry.getKey(), file);			
		}
		
	}
	
	//Cache Disk' files
	public static void calPriInCacheDisk(DiskInfo disk, int totalRequests){
		Iterator<Entry<String, FileInfo>> iter = disk.getFilesList().entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, FileInfo> entry = iter.next();
			FileInfo file = entry.getValue();

			file.setPriority(((double)file.getRequestNum())/totalRequests);
			
			disk.getFilesList().put(entry.getKey(), file);			
		}
	}
	
	//Replacement Strategy
	//Data Disk====>>SSD Cache Replacement Strategy
	public static void DDtoSSDReplacement(DiskInfo disk0, DiskInfo disk1){
		
		Iterator<Entry<String, FileInfo>> iter = disk1.getFilesList().entrySet().iterator();
		while (iter.hasNext()){
			Entry<String, FileInfo> entry = iter.next();
			FileInfo file = entry.getValue();
			
			if(file.getIsHit()==1){
				disk0.getFilesList().put(entry.getKey(), file);
				disk0.setLeftSpace(disk0.getLeftSpace()-file.getSize());
			}else {
				if(file.getPriority()>=highThreshold){
					disk0.getFilesList().put(entry.getKey(), file);
					disk0.setLeftSpace(disk0.getLeftSpace()-file.getSize());
				}
			}
		}
		

		
	}
	
	//Data Disk====>>SSD Cache and HDD Cache Replacement Strategy
	public static void cacheReplacement(DiskInfo disk0, DiskInfo disk1, DiskInfo disk2){
		
		if(disk0.getLeftSpace()<=5000){
			clearFileList(disk0.getFilesList());
		}
		
		Iterator<Entry<String, FileInfo>> iter = disk2.getFilesList().entrySet().iterator();
		while (iter.hasNext()){
			Entry<String, FileInfo> entry = iter.next();
			FileInfo file = entry.getValue();
			
			if(file.getIsHit()==1){
				disk0.getFilesList().put(entry.getKey(), file);        //The file is hit in SSD Cache
				disk0.setLeftSpace(disk0.getLeftSpace()-file.getSize());
			}else {
				if(file.getPriority()>=highThreshold){
					disk0.getFilesList().put(entry.getKey(), file);    //The file is associated in SSD Cache
					disk0.setLeftSpace(disk0.getLeftSpace()-file.getSize());
				}else if(file.getPriority()>=normalThreshold){
					disk1.setDiskState(1);
					disk1.getFilesList().put(entry.getKey(), file);    //The file is associated in HDD Cache
					disk1.setLeftSpace(disk1.getLeftSpace()-file.getSize());
				}
			}
		}
		
		
		sortedByPriority(disk0.getFilesList());
		sortedByPriority(disk1.getFilesList());
	}
	
	//&&&&&&&&&&&&&&&
	//HDD Cache====>>SSD Cache Replacement Strategy
	public static void HDDtoSSDReplacement(DiskInfo disk0, DiskInfo disk1){
		
		if(disk0.getLeftSpace()<=5000){
			clearFileList(disk0.getFilesList());
		}
		
		HashMap<String, FileInfo> highPriorityFiles = new HashMap<String, FileInfo>();
		List<String> highPriorityFilesName = new LinkedList<String>();
		
		Iterator<Entry<String, FileInfo>> iter = disk1.getFilesList().entrySet().iterator();
		while (iter.hasNext()){
			Entry<String, FileInfo> entry = iter.next();
			FileInfo file = entry.getValue();

			if(file.getPriority()>highThreshold){
				highPriorityFiles.put(file.getFileName(), file);
				highPriorityFilesName.add(file.getFileName());
			}
			
		}
		
		
		disk0.getFilesList().putAll(highPriorityFiles);
		for(int i=0; i<highPriorityFilesName.size(); i++){
			disk1.getFilesList().remove(highPriorityFilesName.get(i));
		}
		
		
		sortedByPriority(disk0.getFilesList());
		sortedByPriority(disk1.getFilesList());
	}
	

	//HDD Cache====>>HDD Cache Replacement Strategy
	public static void HDDtoHDDReplacement(DiskInfo[] disks){
		
		for(int i=0; i<disks.length; i++){
			if(disks[i].getDiskState() == 1){
				sortedByPriority(disks[i].getFilesList());
			}
		}

		
		HashMap<String, FileInfo> totalFiles = new HashMap<String, FileInfo>();
		
		for(int i=0; i<disks.length; i++){
			if(disks[i].getDiskState() == 1){
				totalFiles.putAll(disks[i].getFilesList());
			}
		}
		
		sortedByPriority(totalFiles);
		
		clearFileList(totalFiles);            //Clear FileList
		
		for (int i=0; i<disks.length; i++) {
			disks[i].getFilesList().clear();      //Clear FileList		
		}
		
		DiskStateStat.clearHDD(disks);
		
		
		Iterator<Entry<String, FileInfo>> iterTotal = totalFiles.entrySet().iterator();
		
		int flag = 0;
		
		while (iterTotal.hasNext()){
			Entry<String, FileInfo> entry = iterTotal.next();
			
			if(disks[flag].getLeftSpace()>=200){
				disks[flag].setDiskState(1);
				disks[flag].getFilesList().put(entry.getKey(), entry.getValue());
				disks[flag].setLeftSpace(disks[flag].getLeftSpace()-entry.getValue().getSize());
			}else {
				disks[++flag].setDiskState(1);
				disks[flag].getFilesList().put(entry.getKey(), entry.getValue());
				disks[flag].setLeftSpace(disks[flag].getLeftSpace()-entry.getValue().getSize());	
			}
		}
		
		for(int i=0; i<disks.length; i++){
			if(disks[i].getDiskState() == 1){
				sortedByPriority(disks[i].getFilesList());
			}
		}		
	}
	
	
	//Clear the cache
	public static void clearCache(DiskInfo disk0){
		sortedByPriority(disk0.getFilesList());
		Iterator<Entry<String, FileInfo>> iter = disk0.getFilesList().entrySet().iterator();
		List<String> lowPriorityFiles = new LinkedList<String>();
		while (iter.hasNext()){
			Entry<String, FileInfo> entry = iter.next();
			FileInfo file = entry.getValue();

			if(file.getPriority()<lightThreshold){
				lowPriorityFiles.add(file.getFileName());    //The file is associated in HDD Cache
			}		
		}
		
		for(int i=0; i<lowPriorityFiles.size(); i++){
			disk0.setLeftSpace(disk0.getLeftSpace()+disk0.getFilesList().get(lowPriorityFiles.get(i)).getSize());			
			disk0.getFilesList().remove(lowPriorityFiles.get(i));
		}
	}
	
	
	//Clear the FileList
	public static void clearFileList(HashMap<String, FileInfo> fileList){
		List<Entry<String, FileInfo>> list = new ArrayList<Entry<String,FileInfo>>(fileList.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, FileInfo>>(){
			                       public int compare(Map.Entry<String, FileInfo> e1, Map.Entry<String, FileInfo> e2){         	   
			                    	   double v1 = (double) ((Map.Entry<String, FileInfo>)e1).getValue().getPriority(); 
			                    	   double v2 = (double) ((Map.Entry<String, FileInfo>)e2).getValue().getPriority(); 
			                    	   double flag = v2-v1;
			                    	   if(flag > 0.0){
			                    		   return 1;
			                    	   }else if(flag == 0){
										return 0;
									   }else {
										return -1;
									   }			                   
			           				  }
			                       }); 
		
		
		
        for(int i=list.size()-1; i>=list.size()*9/10; i--){
        	fileList.remove(list.get(i).getKey());
		}

	}
	
	
	//Refresh All
	public static void refresh(InitEnvironment init, int requestNum, int totalRequestNum){
		//Timing Empty Request
		if(requestNum >= Integer.parseInt(ReadXml.readname("config/RefreshTime.xml", "refreshtime"))-1){
			
			ReplacementStrategy.HDDtoHDDReplacement(init.getSecLevCache());
			
			ReplacementStrategy.clearFileList(init.getSSDDisk().getFilesList());
			
			DiskStateStat.refreshRequest(init.getSSDDisk());
			for(int i=0; i<init.getSecLevCache().length; i++){
				if(init.getSecLevCache()[i].getDiskState() == 1){
					DiskStateStat.refreshRequest(init.getSecLevCache()[i]);
				}
			}
			
			totalRequestNum = 0;
			requestNum = 0;
		}
	}
	
	//Refresh All
	public static void refresh(InitEnvironmentPB init, int requestNum, int totalRequestNum){
		//Timing Empty Request
		if(requestNum >= Integer.parseInt(ReadXml.readname("config/RefreshTime.xml", "refreshtime"))-1){
			
			ReplacementStrategy.HDDtoHDDReplacement(init.getSecLevCache());
			
			ReplacementStrategy.clearCache(init.getSSDDisk());
			
			DiskStateStat.refreshRequest(init.getSSDDisk());
			for(int i=0; i<init.getSecLevCache().length; i++){
				if(init.getSecLevCache()[i].getDiskState() == 1){
					DiskStateStat.refreshRequest(init.getSecLevCache()[i]);
				}
			}
			
			totalRequestNum = 0;
			requestNum = 0;
		}
	}
	
	//Replacement Strategy
	//Data Disk====>>SSD Cache Replacement Strategy of MAID
	public static void MAIDReplacement(DiskInfo disk0, DiskInfo disk1){
		
		Iterator<Entry<String, FileInfo>> iter = disk1.getFilesList().entrySet().iterator();
		while (iter.hasNext()){
			Entry<String, FileInfo> entry = iter.next();
			FileInfo file = entry.getValue();
			
			if(file.getIsHit()==1){
				disk0.getFilesList().put(entry.getKey(), file);
				disk0.setLeftSpace(disk0.getLeftSpace()-file.getSize());
			}
		}	
	}
	
	//Data Disk====>>HDD Cache Replacement Strategy of MAID
	public static void MAIDCacheReplacement(DiskInfo disk0, DiskInfo disk1){
			
		Iterator<Entry<String, FileInfo>> iter = disk1.getFilesList().entrySet().iterator();
		while (iter.hasNext()){
			Entry<String, FileInfo> entry = iter.next();
			FileInfo file = entry.getValue();
			
			if(file.getIsHit()==1){
				disk0.getFilesList().put(entry.getKey(), file);        //The file is hit in SSD Cache
				disk0.setLeftSpace(disk0.getLeftSpace()-file.getSize());
			}
		}
			
		sortedByPriority(disk0.getFilesList());
		sortedByPriority(disk1.getFilesList());
	}

}
