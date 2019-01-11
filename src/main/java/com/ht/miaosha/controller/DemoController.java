package com.ht.miaosha.controller;

import com.ht.miaosha.entity.User;
import com.ht.miaosha.rabbitmq.MQReceiver;
import com.ht.miaosha.rabbitmq.MQSender;
import com.ht.miaosha.redis.RedisService;
import com.ht.miaosha.redis.UserKey;
import com.ht.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.result.Result;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/demo")
public class DemoController {

	@Autowired
	public UserService userService;


	@Autowired
	public RedisService redisServie;

	@Autowired
	public MQSender mqSender;

	@Autowired
	public MQReceiver mqReceiver;

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}

	//1.rest api json输出 2.页面
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello() {
		return Result.success("hello,sdimooc");
	}

	@RequestMapping("/mq")
	@ResponseBody
	public Result mq() {
		mqSender.send("hello rabbitmq");
		mqSender.sendTopic("hello rabbitmq");
		return Result.success("success");
	}

	@RequestMapping("/mq/topic")
	@ResponseBody
	public Result mq_topic() {
		mqSender.sendTopic("hello rabbitmq");
		return Result.success("success");
	}

	@RequestMapping("/mq/fanout")
	@ResponseBody
	public Result mq_fanout() {
		mqSender.sendFanout("hello rabbitmq");
		return Result.success("success");
	}

	@RequestMapping("/mq/header")
	@ResponseBody
	public Result mq_header() {
		mqSender.sendHeader("hello header");
		return Result.success("success");
	}

	@RequestMapping("/helloError")
	@ResponseBody
	public Result<String> helloError() {
		return Result.error(CodeMsg.SERVER_ERROR);
		//return new Result(500102, "XXX");
	}

//	    返回字符串也就是跳转地址
	@GetMapping("/show")
	public String show(Model model, HttpServletRequest request) {
		model.addAttribute("name", "Joshua");
		System.out.println(request.getRequestURI());
		return "hello";
	}

	@GetMapping("/db/get")
	@ResponseBody
	public Result<User> dbGet(Model model) {
		User user = userService.getById(1);
		System.out.println(user);
		return Result.success(user);
	}

	@GetMapping("/db/tx")
	@ResponseBody
	public String dbTx(Model model) {
		userService.tx();
		return "baby";
	}

	@RequestMapping("/redis/get")
	@ResponseBody
	public Result<User> getRedisData() {
		boolean v = redisServie.set(UserKey.getById, "key2", userService.getById(1));
		if(v) {
			System.out.println(redisServie.get(UserKey.getById, "key1", User.class));
			return Result.success(redisServie.get(UserKey.getById, "key2", User.class));
		}
		return Result.success(null);
	}


}
