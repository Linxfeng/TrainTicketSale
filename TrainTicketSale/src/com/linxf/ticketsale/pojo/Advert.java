package com.linxf.ticketsale.pojo;

import java.io.Serializable;

/**
 * 广告/公告/通知
 * 
 * @author lintao
 *
 */
public class Advert implements Serializable {
	private static final long serialVersionUID = 1L;
	private int aid;// 广告编号
	private String title;// 广告标题
	private String context;// 广告内容
	private String sendtime;// 发送日期
	private int del;// 是否删除：0代表不删除，1代表已删除

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public int getDel() {
		return del;
	}

	public void setDel(int del) {
		this.del = del;
	}

}
