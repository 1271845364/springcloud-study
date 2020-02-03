package com.yejinhui.controller;

import java.util.UUID;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.yejinhui.service.DeferredResultQueue;

@Controller
public class AsyncController {
	
	@ResponseBody
	@RequestMapping("/createOrder")
	public DeferredResult<Object> createOrder() {
		DeferredResult<Object> deferredResult = new DeferredResult<Object>((long)3000, "create fail ...");
		DeferredResultQueue.save(deferredResult);
		return deferredResult;
	}
	
	@ResponseBody
	@RequestMapping("/create")
	public String create() {
		DeferredResult<Object> deferredResult = DeferredResultQueue.get();
		String orderId = UUID.randomUUID().toString();
		deferredResult.setResult(orderId);
		return "success=====>>>> " + orderId; 
	}

	/**
	 * 1、控制器返回Callable
	 * 2、Spring异步处理，将Callable提交到 TaskExecutor 使用一个隔离的线程执行
	 * 3、DispatcherServlet和所有的Filter退出Web容器的线程，但是Response保持打开状态
	 * 4、Callable返回结果，SpringMVC将请求重新派发给容器，恢复之前的处理
	 * 5、根据Callable返回的结果。SpringMVC继续进行视图渲染流程等（从收请求-视图渲染）
	 * 
	 *  preHandle.../springmvc-annotation/async01
		主线程开始...Thread[http-nio-8080-exec-2,5,main]==>>>1535617452916
		主线程结束...Thread[http-nio-8080-exec-2,5,main]==>>>1535617452918
		=====================DispatcherServlet及所有的Filter退出线程========================================
		
		=====================等待Callable执行======================================================
		副线程开始...Thread[MvcAsync1,5,main]==>>>1535617452925
		副线程结束...Thread[MvcAsync1,5,main]==>>>1535617455926
		=====================Callable执行完成===============================================================
		
		==========================================================================================
		preHandle.../springmvc-annotation/async01
		postHandle...（Callable的之前的返回值就是目标方法的返回值）
		afterCompletion...
		
		异步的拦截器：
			1）、原生的API的AsyncListener
			2）、SpringMVC：实现AsyncHandlerInterceptor 
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/async01")
	public Callable<String> async01() {
		System.out.println("主线程开始..." + Thread.currentThread() + "==>>>" + System.currentTimeMillis());
		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				System.out.println("副线程开始..." + Thread.currentThread() + "==>>>" + System.currentTimeMillis());
				Thread.sleep(3000);
				System.out.println("副线程结束..." + Thread.currentThread() + "==>>>" + System.currentTimeMillis());
				return "Callable<String> async01()";
			}
		};
		System.out.println("主线程结束..." + Thread.currentThread() + "==>>>" + System.currentTimeMillis());
		return callable;
	}
}