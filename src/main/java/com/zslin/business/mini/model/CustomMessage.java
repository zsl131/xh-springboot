package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 客服消息
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_custom_message")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String openid;

	private Integer customId;

	private String nickname;

	/**
	* text;image
	*/
	private String msgType;

	private String msgId;

	@Lob
	private String content;

	private String headImgUrl;

	private String createDay;

	private String createTime;

	private Long createLong;

	private String picUrl;

	private String mediaId;

	private String createTimeMini;

	private String replyDay;

	private String replyTime;

	private Long replyLong;

	private String reply;

}
