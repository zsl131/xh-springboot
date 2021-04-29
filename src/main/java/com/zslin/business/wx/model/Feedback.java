package com.zslin.business.wx.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 微信反馈
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "wx_feedback")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String createDay;

	private String createTime;

	private Long createLong;

	private String openid;

	private Integer accountId;

	private String headImgUrl;

	@Lob
	private String content;

	private String nickname;

	private String status="0";

	@Lob
	private String reply;

	private String replyDay;

	private String replyTime;

	private Long replyLong;

}
