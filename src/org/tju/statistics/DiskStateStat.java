package org.tju.statistics;

import java.util.Iterator;
import java.util.Map.Entry;

import org.tju.bean.DiskInfo;
import org.tju.bean.FileInfo;

/**
 * Name: main
 * Description:
 * 
 * @author yuan
 * @date 2014年10月21日 上午10:50:38
 */
public class DiskStateStat {
	
	final public static int Tbe = 10;
	
	public static int diskStateStat(DiskInfo[] disks){
		int openDiskNum = 0;
		for(int i=0; i<disks.length; i++){
			if(disks[i].getDiskState() == 1){
				openDiskNum++;
			}
		}
		return openDiskNum;		
	}
	
	public static void idleTimeStat(DiskInfo[] disks){
		for(int i=0; i<disks.length; i++){
			if(disks[i].getDiskState() == 1){
				disks[i].setIdleTime(disks[i].getIdleTime() + 1);
			}
		}
	}
	
	public static void modifyDiskState(DiskInfo disk){
		if(disk.getDiskState() == 1){
			disk.setIdleTime(0);
		}else{
			disk.setDiskState(1);		
		}	
	}
	
	//Clear HDD Disks
	public static void clearHDD(DiskInfo[] disks){
		for(int i=0; i<disks.length; i++){
			disks[i].setDiskState(0);
			disks[i].setLeftSpace(disks[i].getTotalSpace());
			disks[i].setIdleTime(0);
		}
	}
	
	
	//Before close the disk, refresh the files' priority
	public static void reFresh(DiskInfo disk){
		if(disk.getDiskState()==1){
			Iterator<Entry<String, FileInfo>> iter = disk.getFilesList().entrySet().iterator();
			
			while (iter.hasNext()) {
				Entry<String, FileInfo> entry = iter.next();
				FileInfo file = entry.getValue();
				file.setRequestNum(0);
				file.setPriority(0);
				
				disk.getFilesList().put(entry.getKey(), file);			
			}
		}
		
		disk.setIdleTime(0);
		disk.setDiskState(0);
		
	}
	
	public static void closeDisk(DiskInfo[] disks){
		for(int i=0; i<disks.length; i++){
			if(disks[i].getIdleTime() >= Tbe){
				reFresh(disks[i]);
			}
		}
	}
	
	
	//Refresh The RequestNum of disks
	public static void refreshRequest(DiskInfo disk){
		if(disk.getDiskState()==1){
			Iterator<Entry<String, FileInfo>> iter = disk.getFilesList().entrySet().iterator();
			
			while (iter.hasNext()) {
				Entry<String, FileInfo> entry = iter.next();
				FileInfo file = entry.getValue();
				file.setRequestNum(0);
				file.setPriority(0);
				
				disk.getFilesList().put(entry.getKey(), file);			
			}
		}
	}

}
