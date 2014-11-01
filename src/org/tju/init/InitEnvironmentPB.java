package org.tju.init;

import java.util.HashMap;

import org.tju.bean.DiskInfo;
import org.tju.bean.FileInfo;
import org.tju.tool.ReadXml;

/**
 * Name: InitEnvironmentPB
 * Description: Initialization Simulation Environment for PB Group
 * 
 * @author yuan
 * @date 2014年10月29日 下午20:37:08
 */
public class InitEnvironmentPB {
	
	public int fileId = 0;
	public int skyZone = 0;
	public int tatalObsTime = 600;
	
	public int diskIndex = 0;
	
	public DiskInfo SSDDisk;
	public DiskInfo[] secLevCache = new DiskInfo[4];        //4 HDD Disks
	
	public DiskInfo[] dataDisks = new DiskInfo[15];         //15 Data Disks
	
	
	public void initEnvironment(int groupId){
		fileId = groupId * 117000;
		skyZone = groupId * 195;
		diskIndex = groupId * 15;
		//Initialize Data Disks 
		for(int i=0; i<15; i++){
			int usedSpace = 0;
			HashMap<String, FileInfo> filesList = new HashMap<String, FileInfo>();
			for( ; skyZone<(13*(i+1)+groupId*195); skyZone++){
				for(int k=0; k<tatalObsTime; k++){
					String fileName = String.valueOf(i+diskIndex)+"-"+String.valueOf(skyZone)+"-"+String.valueOf(k);
					FileInfo file = new FileInfo(fileId++, fileName, Integer.valueOf(ReadXml.readname("config/FileInfo.xml", "size")), 
							                        k, skyZone, i, 0, 0, 0);
					filesList.put(fileName, file);
					usedSpace += file.getSize();
					
				}
			}
			
			dataDisks[i] = new DiskInfo(i+diskIndex, 2, 0, Double.valueOf(ReadXml.readname("config/HDDDisk.xml", "size")), 
                                           Double.valueOf(ReadXml.readname("config/HDDDisk.xml", "size"))-usedSpace, 0, 
                                           Double.valueOf(ReadXml.readname("config/HDDDisk.xml", "operpower")), filesList);

			System.out.println("====>>"+fileId);
			System.out.println("====>>"+skyZone);
		}
		
		//Initialize Data Disks End
		System.out.println("========>>>>Initialize Data Disks Group-" + groupId +" Success!!!<<<<========");
		
		
		//Initialize First Level Cache Disk
		HashMap<String, FileInfo> SSDFilesList = new HashMap<String, FileInfo>();
		SSDDisk = new DiskInfo(1000, 0, 0, Double.valueOf(ReadXml.readname("config/SSDDisk.xml", "size")), 
                                  Double.valueOf(ReadXml.readname("config/SSDDisk.xml", "size")), 0, 
                                  Double.valueOf(ReadXml.readname("config/SSDDisk.xml", "operpower")), SSDFilesList);

		//Initialize First Level Cache Disk End
		System.out.println("========>>>>Initialize SSD Disk Success!!!<<<<========");
				
		
		//Initialize Second Level Cache Disk
		for(int i=1001; i<1005; i++){
			HashMap<String, FileInfo> HDDFilesList = new HashMap<String, FileInfo>();
			secLevCache[i-1001] = new DiskInfo(i, 1, 0, Double.valueOf(ReadXml.readname("config/HDDDisk.xml", "size")),
					                             Double.valueOf(ReadXml.readname("config/HDDDisk.xml", "size")), 0, 
					                             Double.valueOf(ReadXml.readname("config/HDDDisk.xml", "operpower")), HDDFilesList);
		}
		
		//Initialize First Level Cache Disk End
		System.out.println("========>>>>Initialize HDD Disks Success!!!<<<<========");
				
		
		//Initialize Environment End
		System.out.println("========>>>>Initialize Environment Success!!!<<<<========");
	}

	
	/**
	 * @return the sSDDisk
	 */
	public DiskInfo getSSDDisk() {
		return SSDDisk;
	}



	/**
	 * @param sSDDisk the sSDDisk to set
	 */
	public void setSSDDisk(DiskInfo sSDDisk) {
		SSDDisk = sSDDisk;
	}



	/**
	 * @return the secLevCache
	 */
	public DiskInfo[] getSecLevCache() {
		return secLevCache;
	}



	/**
	 * @param secLevCache the secLevCache to set
	 */
	public void setSecLevCache(DiskInfo[] secLevCache) {
		this.secLevCache = secLevCache;
	}



	/**
	 * @return the dataDisks
	 */
	public DiskInfo[] getDataDisks() {
		return dataDisks;
	}



	/**
	 * @param dataDisks the dataDisks to set
	 */
	public void setDataDisks(DiskInfo[] dataDisks) {
		this.dataDisks = dataDisks;
	}


}
