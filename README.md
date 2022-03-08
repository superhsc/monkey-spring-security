# monkey-spring-security

Spring Security 是 Spring 家族中历史悠久的框架，在 Spring Boot 之前出现，具备完整强大的功能体系。
但 Spring Security 发展一直不顺利。主要问题在于：<b>继承</b>、 <b>配置</b>过程较为复杂。

随着 Spring Boot 的兴起，基于 Spring Boot 所提供针对 Spring Security 的自动配置方案，可以零配置使用 Spring Security 。

## 1. Spring Security 初探
如果想要在 Spring Boot 中使用 Spring Security ，只需要在 Maven 工程的 pom 文件中添加如下依赖：
```java
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

下面的例子是通过构建一个简单 HTTP 端点，演示 Spring Security 的初步使用。
```java
@RestController
public class DemoController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}

```

启动上面的程序，然后通过浏览器访问 "/hello" 端点。
可能希望得到的是 "Hello World!" 这个输出结果，
但事实上，浏览器跳转到一个如下所示的登录页面：

![image.png](https://cdn.nlark.com/yuque/0/2021/png/12442250/1628949521145-3f8986c8-7d50-4d0d-b6ac-2ef615d60a5f.png#crop=0&crop=0&crop=1&crop=1&height=174&id=PJd5G&margin=%5Bobject%20Object%5D&name=image.png&originHeight=694&originWidth=1663&originalType=binary&ratio=1&rotation=0&showTitle=false&size=64575&status=done&style=none&title=&width=416)

会弹出上图所示的登录页面原因在于：
- 在添加 Spring Security 依赖之后，Spring Security 为应用程序**自动嵌入了用户认证机制**。


接下来，注意观察项目在 IDEA 控制台启动日志中，出现了如图下所示的一行日志：
![image.png](https://cdn.nlark.com/yuque/0/2021/png/12442250/1628949382586-2bdd2d9a-2a67-4c55-9287-c2ace375419d.png#crop=0&crop=0&crop=1&crop=1&height=231&id=SL54q&margin=%5Bobject%20Object%5D&name=image.png&originHeight=924&originWidth=2244&originalType=binary&ratio=1&rotation=0&showTitle=false&size=355962&status=done&style=none&title=&width=561)

这行日志就是 Spring Security 生产的一个密码，而用户名则是系统默认的 "user"。
通过输入正确的用户名和密码，浏览器就会输出 "Hello World！"这个响应结果。

上面就是 Spring Security 提供的认证功能，也是 Spring Security 众多功能中的一项基础功能。


## 2. Spring Security 功能体系

Spring Security 提供的是一整套完整的安全性解决方案。面向不同的业务需求和应用场景，Spring Security 分别提供了对应的安全策略。
下面分别从**单体应用、微服务架构以及响应式系统**这三个维度对这些功能进行总结。


### 2.1 Spring Security 与单体应用


在软件系统中，把需要访问的内容称为**资源（Resource）, 

**安全性设计的目的是对这些资源进行保护，确保它们的访问是安全性的。

例如：在一个 Web 应用程序中，对外暴露的 HTTP 端点就可以被理解为是资源。

对于资源的安全性访问，业界存在一些常见的技术体系，其中存在既容易理解又容易混淆的两个概念：

- **认证（Authentication）**
- **授权（Authorization）**

​

认证和授权结合起来，**构成了对系统中的资源进行安全性管理的最常见的解决方案 **：

1. 判断资源访问者的有效身份
1. 确定其是否有对这个资源进行访问的合法权限

这个流程如下图所示：
![基于认证和授权机制的资源访问安全性示意图.png](https://cdn.nlark.com/yuque/0/2021/png/12442250/1628950663193-be8c2579-a3ac-49df-891e-1c0e8fcb8911.png#crop=0&crop=0&crop=1&crop=1&height=236&id=Y6MIe&margin=%5Bobject%20Object%5D&name=%E5%9F%BA%E4%BA%8E%E8%AE%A4%E8%AF%81%E5%92%8C%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6%E7%9A%84%E8%B5%84%E6%BA%90%E8%AE%BF%E9%97%AE%E5%AE%89%E5%85%A8%E6%80%A7%E7%A4%BA%E6%84%8F%E5%9B%BE.png&originHeight=942&originWidth=1424&originalType=binary&ratio=1&rotation=0&showTitle=true&size=72188&status=done&style=none&title=%E5%9F%BA%E4%BA%8E%E8%AE%A4%E8%AF%81%E5%92%8C%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6%E7%9A%84%E8%B5%84%E6%BA%90%E8%AE%BF%E9%97%AE%E5%AE%89%E5%85%A8%E6%80%A7%E7%A4%BA%E6%84%8F%E5%9B%BE&width=356 "基于认证和授权机制的资源访问安全性示意图")
​

上图代表一种通用方案，该方案面对不同应用场景和技术体系可以衍生出很多具体的实现策略。
​

Web 应用系统中的认证和授权模型与上图相似。但在具体设计和实现过程中也有其特殊性。
​


- 针对认证。这部分的需求相对比较明显，有以下两个要点
    - 构建一套完整的存储体系，保存和维护用户信息，
    - 确保在处理请求过程中合理利用这些用户信息。
- 针对授权。情况相对认证较复杂。对于某一特定 Web 应用程序而言，面临两个主要问题：
    - 判断一个 HTTP 请求是否具备访问权限
    - 对访问进行精细化管理（虽然一个请求具备访问该应用程序的权限，但是不意味着能够访问所有 HTTP 端点，某些核心功能需要具备较高的权限才能访问，而有些则不需要）

如下图所示：
![Web 应用程序访问授权效果示意图.png](https://cdn.nlark.com/yuque/0/2021/png/12442250/1628951048023-a9c8d51f-466e-4688-bdf7-a83f12289066.png#crop=0&crop=0&crop=1&crop=1&height=323&id=h1snU&margin=%5Bobject%20Object%5D&name=Web%20%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F%E8%AE%BF%E9%97%AE%E6%8E%88%E6%9D%83%E6%95%88%E6%9E%9C%E7%A4%BA%E6%84%8F%E5%9B%BE.png&originHeight=1292&originWidth=2020&originalType=binary&ratio=1&rotation=0&showTitle=true&size=132449&status=done&style=none&title=Web%20%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F%E8%AE%BF%E9%97%AE%E6%8E%88%E6%9D%83%E6%95%88%E6%9E%9C%E7%A4%BA%E6%84%8F%E5%9B%BE&width=505 "Web 应用程序访问授权效果示意图")
​

在上图中，假设该请求具备对应用程序中端点2、3、4 的访问权限，但是不具备访问端点 1 的权限。想要达到这个效果，一般的做法**引入角色体系**

- 针对不同的用户设置不同等级的角色
- 角色等级对应的访问权限等级
- 每一个请求绑定到某一个角色，也就具备了某一个权限的访问



接下来，把认证和授权结合起来，梳理出 Web 应用程序访问场景下的安全性实现方案，如下图所示：
![单体服务下的认证和授权整合示意图 (1).png](https://cdn.nlark.com/yuque/0/2021/png/12442250/1628951456284-1310bf7b-80fa-45a4-93ca-437d961d98e7.png#crop=0&crop=0&crop=1&crop=1&height=1069&id=gRzlm&margin=%5Bobject%20Object%5D&name=%E5%8D%95%E4%BD%93%E6%9C%8D%E5%8A%A1%E4%B8%8B%E7%9A%84%E8%AE%A4%E8%AF%81%E5%92%8C%E6%8E%88%E6%9D%83%E6%95%B4%E5%90%88%E7%A4%BA%E6%84%8F%E5%9B%BE%20%281%29.png&originHeight=1069&originWidth=1908&originalType=binary&ratio=1&rotation=0&showTitle=true&size=102114&status=done&style=none&title=%E5%8D%95%E4%BD%93%E6%9C%8D%E5%8A%A1%E4%B8%8B%E7%9A%84%E8%AE%A4%E8%AF%81%E5%92%8C%E6%8E%88%E6%9D%83%E6%95%B4%E5%90%88%E7%A4%BA%E6%84%8F%E5%9B%BE&width=1908 "单体服务下的认证和授权整合示意图")
结合示意图可以看到：

- 通过请求传递用户凭证完成用户认证
- 根据该用户信息中具备的角色信息获取访问权限，
- 最终完成对 HTTP 端点的访问授权



围绕认证和授权，还需要一系列的额外功能确保整个流程得以实现。这些功能包括：

- 用于密码保护的加解密机制
- 用于实现方法级的安全访问
- 支持跨域等

这些功能在以后再总结。



- [Spring Security 是一个怎样的框架](#Spring Security 是一款怎样的安全框架) 
