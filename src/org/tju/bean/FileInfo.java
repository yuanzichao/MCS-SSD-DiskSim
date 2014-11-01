package org.tju.bean;

/**
 * Name: FileInfo
 * Description: Files' basic Information
 * 
 * @author yuan
 * @date 2014年10月19日 下午2:17:13
 */
public class FileInfo {
	
	private int fileId;
	private String fileName;
	private int size;
	private int observeTime;
	private int skyZone;
	private int diskInId;
	private int isHit;                  //(0,1)is(Miss,Hit)
	private int requestNum;
	private double priority;
	
	
	public FileInfo(int fileId, String fileName, int size){
		this.fileId = fileId;
		this.fileName = fileName;
		this.size = size;	
	}
	
	public FileInfo(int fileId, String fileName, int size, int observeTime, int skyZone,  
			            int diskInId, int isHit, int requestNum, double priority){
		this.fileId = fileId;
		this.fileName = fileName;
		this.size = size;
		this.observeTime = observeTime;
		this.skyZone = skyZone;
		this.diskInId = diskInId;
		this.isHit = isHit;
		this.requestNum = requestNum;
		this.priority = priority;
	}
	

	/**
	 * @return the fileId
	 */
	public int getFileId() {
		return fileId;
	}

	/**
	 * @param fileId the fileId to set
	 */
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the observeTime
	 */
	public int getObserveTime() {
		return observeTime;
	}

	/**
	 * @param observeTime the observeTime to set
	 */
	public void setObserveTime(int observeTime) {
		this.observeTime = observeTime;
	}

	/**
	 * @return the skyZone
	 */
	public int getSkyZone() {
		return skyZone;
	}

	/**
	 * @param skyZone the skyZone to set
	 */
	public void setSkyZone(int skyZone) {
		this.skyZone = skyZone;
	}

	/**
	 * @return the isHit
	 */
	public int getIsHit() {
		return isHit;
	}

	/**
	 * @param isHit the isHit to set
	 */
	public void setIsHit(int isHit) {
		this.isHit = isHit;
	}

	/**
	 * @return the diskInId
	 */
	public int getDiskInId() {
		return diskInId;
	}

	/**
	 * @param diskInId the diskInId to set
	 */
	public void setDiskInId(int diskInId) {
		this.diskInId = diskInId;
	}

	/**
	 * @return the requestNum
	 */
	public int getRequestNum() {
		return requestNum;
	}

	/**
	 * @param requestNum the requestNum to set
	 */
	public void setRequestNum(int requestNum) {
		this.requestNum = requestNum;
	}

	/**
	 * @return the priority
	 */
	public double getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(double priority) {
		this.priority = priority;
	}

}
