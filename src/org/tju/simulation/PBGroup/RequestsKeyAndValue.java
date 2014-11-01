package org.tju.simulation.PBGroup;

/**
 * @author yuan
 *
 * @date 2014年10月29日 下午10:39:13
 */
public class RequestsKeyAndValue{
	public int diskId;
	public String fileName;
	
	public RequestsKeyAndValue(int diskId, String fileName){
		this.diskId = diskId;
		this.fileName = fileName;
	}
	
	/**
	 * @return the diskId
	 */
	public int getDiskId() {
		return diskId;
	}
	/**
	 * @param diskId the diskId to set
	 */
	public void setDiskId(int diskId) {
		this.diskId = diskId;
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
	
	
	
}
