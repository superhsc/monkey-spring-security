对于微服务架构而言，安全性设计的最核心考虑点还是**认证和授权**。
​

由于一个微服务系统中各服务之间，存在相互调用的关系，因此针对每一个服务，需要考虑下面两点：

- 自客户端的请求，
- 来自另一个服务的请求

​

因此，安全访问控制也面临以下两点：

- 从客户端请求到服务
- 从服务到服务等多种授权场景

​

为此，需要引入专门用于处理分布式环境下的授权体系，OAuth2 协议就是应对这种应用场景的有效解决方案。
# OAuth2 协议详解

- Open Authorization 的简称
- **该协议解决的是授权问题，**而不是认证问题
- 目前普遍被采用的是 OAuth 2.0 版协议。
   - OAuth2 是一个相对复杂的协议，对涉及的角色和授权模式给出了明确定义
## OAuth2 协议的应用场景


在常见的电商系统中，存在类似工单处理的系统，工单的生成，在使用用户基本信息的同时，势必也依赖于用户的订单记录等数据。
​

为降低开发成本，假设整个商品订单模块不是自己研发的，而是集成了外部的订单管理平台，此时为生成工单记录，就必须让工单系统读取用户在订单管理平台上的订单记录。
​

在这个场景中，难点在于**只有得到用户的授权**，才能同意工单系统读取用户在订单管理平台上的订单记录。
​

此时问题就来了，工单系统如何获得用户的授权呢？一般能够想到的方法是：

- 用户将自己在订单管理平台上的用户名和密码告诉工单系统，
- 工单系统通过用户名和密码登录到订单管理平台并读取用户的订单记录



整个过程如下图所示：
![案例系统中用户认证和授权交互示意图 (2).png](https://cdn.nlark.com/yuque/0/2022/png/12442250/1646643401616-4d456c27-8ef8-4a9a-b180-b868097db0b6.png#clientId=u47ad442f-2909-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=uf521adf8&margin=%5Bobject%20Object%5D&name=%E6%A1%88%E4%BE%8B%E7%B3%BB%E7%BB%9F%E4%B8%AD%E7%94%A8%E6%88%B7%E8%AE%A4%E8%AF%81%E5%92%8C%E6%8E%88%E6%9D%83%E4%BA%A4%E4%BA%92%E7%A4%BA%E6%84%8F%E5%9B%BE%20%282%29.png&originHeight=416&originWidth=770&originalType=binary&ratio=1&rotation=0&showTitle=false&size=26225&status=done&style=none&taskId=u085b7009-e5b9-4611-b86e-d75f71c2602&title=)


上图中的方案虽然可行，但显然存在几个严重的缺点：
​


- 工单系统为了开展后续的服务，会保存用户在订单管理平台上的密码，这样很不安全；如果用户密码不小心被泄露了，就会导致订单管理平台上的用户数据发生泄露；



- 工单系统拥有了获取用户存储在订单管理平台上所有资料的权限，用户无法限制工单系统获得授权的范围和有效期；



- 如果用户修改了订单管理平台的密码，那么工单系统就无法正常访问订单管理平台了，这会导致业务中断，但又不能限制用户修改密码。



既然这个方案存在如此多的问题，那么有没有更好的办法呢？答案是肯定的，**OAuth2 协议**的诞生就是为了解决这些问题。
​

首先，针对密码的安全性，在 OAuth2 协议中：

- 密码还是由用户自己保管，避免了敏感信息的泄露
- OAuth2 协议中提供的授权，具有明确的应用范围和有效期，用户可以根据需要限制工单系统所获取授权信息的作用效果
- 如果用户对自己的密码等身份凭证信息进行了修改，只需通过 OAuth2 协议重新进行一次授权即可，不会影响到相关联的其他第三方应用程序。

​

## OAuth2 协议的角色


OAuth2 协议能够具备这些优势，主要的原因在于**把整个系统涉及的各个角色及其职责做了很好地划分**。
​

OAuth2 协议中定义了四个核心的角色：

- **资源**
- **客户端**
- **授权服务器**
- **资源服务器**

​

![OAuth2 协议中的角色定义 (2).png](https://cdn.nlark.com/yuque/0/2022/png/12442250/1646644691844-ca004fd5-c0b2-436c-8712-9f797d2f3420.png#clientId=u47ad442f-2909-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=u4cfbb344&margin=%5Bobject%20Object%5D&name=OAuth2%20%E5%8D%8F%E8%AE%AE%E4%B8%AD%E7%9A%84%E8%A7%92%E8%89%B2%E5%AE%9A%E4%B9%89%20%282%29.png&originHeight=434&originWidth=779&originalType=binary&ratio=1&rotation=0&showTitle=false&size=39063&status=done&style=none&taskId=u4eb0b92c-6255-43d5-b10b-a26b388c6c2&title=)
​

可以把 OAuth2 中的角色与现实中的应用场景对应起来。

- OAuth2 协议中把需要访问的接口或服务统称为资源（Resource），每个资源都有一个拥有者（Resource Owner），也就是案例中的用户。
- 案例的工单系统代表的是一种第三方应用程序（Third-party Application），通常被称为客户端（Client）。
- 与客户端相对应的，OAuth2 协议中还存在一个服务提供商，案例中的订单管理平台就扮演了这个角色。服务提供商拥有一个**资源服务器**（Resource Server）和一个**授权服务器**（Authorization Server），其中资源服务器存放着用户资源，案例中的订单记录就是一种用户资源；而授权服务器的作用就是完成针对用户的授权流程，并最终颁发一个令牌，也就是我们所说的 Token。



## OAuth2 协议的 Token
令牌是 OAuth2 协议中非常重要的一个概念，本质上是一种**代表用户身份的授权凭证**，
​

但与普通的用户名和密码信息不同，**令牌具有针对资源的访问权限范围和有效期**。
​

如下所示就是一种常见的令牌信息：
```xml
{
  "access_token": "0efa61be-32ab-4351-9dga-8ab668ababae",
  "token_type": "bearer",
  "refresh_token": "738c42f6-79a6-457d-8d5a-f9eab0c7cc5e",
  "expires_in": 43199,
  "scope": "webclient"
}

```
上述令牌信息中的各个字段都很重要，如下表：

| access_token | 代表 OAuth2 的令牌 | 当访问受保护的资源时，用户需要携带这个令牌以便进行验证 |
| --- | --- | --- |
| token_type | 代表令牌类型 | OAuth2 协议中有多种可选的令牌类型，包括 ：
- Bearer 类型（最常见的）
- MAC 类型等
 |
| expires_in | 用于指定 access_token 的有效时间 | 超过这个有效时间，access_token 将会自动失效 |
| refresh_token | 下一个 access_token | 当 access_token 过期后，重新下发一个新的 access_token |
| scope | 可访问的权限范围 | 指定的是访问 Web 资源的“webclient” |

令牌完成基于 OAuth2 协议的授权工作流程。整个流程如下图所示：
​

![基于 OAuth2 协议的授权工作流程图 (2).png](https://cdn.nlark.com/yuque/0/2022/png/12442250/1646644749508-88d950ae-cfe2-4d4e-ae1c-1dd00be33a76.png#clientId=u47ad442f-2909-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=ub17da55c&margin=%5Bobject%20Object%5D&name=%E5%9F%BA%E4%BA%8E%20OAuth2%20%E5%8D%8F%E8%AE%AE%E7%9A%84%E6%8E%88%E6%9D%83%E5%B7%A5%E4%BD%9C%E6%B5%81%E7%A8%8B%E5%9B%BE%20%282%29.png&originHeight=385&originWidth=750&originalType=binary&ratio=1&rotation=0&showTitle=false&size=35997&status=done&style=none&taskId=u2b443050-31d6-4ef8-8acc-f3bd1880940&title=)
​

上述流程如下：

1. 客户端向用户请求授权，请求中一般包含**资源的访问路径**、**对资源的操作类型**等信息；
1. 如果用户同意授权，就会将这个授权返回给客户端；
1. 客户端获取到用户的授权信息，向授权服务器请求访问令牌；
1. 授权服务器向客户端发放访问令牌
1. 客户端携带访问令牌访问资源服务器上的资源
1. 资源服务器获取访问令牌后会验证令牌的有效性和过期时间，并向客户端开放其需要访问的资源。
## OAuth2 协议的授权模式
在整个工作流程中，最为关键的是第二步，即获取用户的有效授权。
在 OAuth 2.0 中，定义了四种授权方式，即：

- **授权码模式（Authorization Code）**
- **简化模式（Implicit）**
- **密码模式（Password Credentials）**
- **客户端模式（Client Credentials）**。

**​**

### 授权码模式（Authorization Code）
​

**当用户同意授权后，授权服务器返回的只是一个授权码，不是最终的访问令牌**。
​

在这种授权模式下，需要客户端携带授权码去换令牌，这就需要客户端自身具备与授权服务器进行直接交互的后台服务。
![授权码模式工作流程图 (2).png](https://cdn.nlark.com/yuque/0/2022/png/12442250/1646645058344-3b05e058-68a4-49e4-8eb3-06a269536163.png#clientId=u47ad442f-2909-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=u129e3307&margin=%5Bobject%20Object%5D&name=%E6%8E%88%E6%9D%83%E7%A0%81%E6%A8%A1%E5%BC%8F%E5%B7%A5%E4%BD%9C%E6%B5%81%E7%A8%8B%E5%9B%BE%20%282%29.png&originHeight=431&originWidth=827&originalType=binary&ratio=1&rotation=0&showTitle=false&size=43169&status=done&style=none&taskId=u8cc062de-b68a-4d4b-914e-576f185be42&title=)
​

授权码模式的执行流程：

1. 用户在访问客户端时，被客户端导向授权服务器
1. 用户可以选择是否给予客户端授权
1. 当用户同意授权，授权服务器通过调用客户端后台服务提供的一个回调地址，将一个授权码返回给客户端
1. 客户端收到授权码后向授权服务器申请令牌
1. 授权服务器核对授权码并向客户端发送访问令牌。



这里要注意的是，**第 4 步是系统自动完成的，不需要用户的参与，用户需要做的就是在流程启动阶段同意授权**。
​

### 密码模式（Password Credentials）
密码模式的授权流程如下图所示：
![密码模式工作流程图 (2).png](https://cdn.nlark.com/yuque/0/2022/png/12442250/1646645367381-909be726-ca2c-422b-a510-6fc6bdfee862.png#clientId=u47ad442f-2909-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=u511fe270&margin=%5Bobject%20Object%5D&name=%E5%AF%86%E7%A0%81%E6%A8%A1%E5%BC%8F%E5%B7%A5%E4%BD%9C%E6%B5%81%E7%A8%8B%E5%9B%BE%20%282%29.png&originHeight=324&originWidth=735&originalType=binary&ratio=1&rotation=0&showTitle=false&size=23789&status=done&style=none&taskId=ud614da46-1470-49a4-82c1-06d42aec54a&title=)
​

可以看到，密码模式比较简单，也更加容易理解，步骤如下：

1. 用户要提供用户名和密码访问客户端，
1. 客户端会基于用户名和密码向授权服务器请求令牌
1. 授权服务器成功执行用户认证操作后将会发放令牌



OAuth2 中的**客户端模式**和**简化模式**因为在日常开发过程中应用得不是很多，就步总结了。
​

此时注意到了，虽然 OAuth2 协议解决的是**授权问题**，但它也应用到了认证的概念，这是因为只有验证了用户的身份凭证，我们才能完成对他的授权。所以说。**OAuth2 是一款技术体系比较复杂的协议，综合应用了信息摘要、签名认证等安全性手段，并需要提供令牌以及背后的公私钥管理等功能**。
​

## OAuth2 协议与微服务架构
对应到微服务系统中，**服务提供者充当的角色就是资源服务器，而服务消费者就是客户端**。所以每个服务本身既可以是客户端，也可以作为资源服务器，或者两者兼之。当客户端拿到 Token 之后，该 Token 就能在各个服务之间进行传递。如下图所示：
![OAuth2 协议在服务访问场景中的应用 (2).png](https://cdn.nlark.com/yuque/0/2022/png/12442250/1646645606290-0616b6ac-2266-4963-a4fe-60470a8a09ac.png#clientId=u47ad442f-2909-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=ud449ef78&margin=%5Bobject%20Object%5D&name=OAuth2%20%E5%8D%8F%E8%AE%AE%E5%9C%A8%E6%9C%8D%E5%8A%A1%E8%AE%BF%E9%97%AE%E5%9C%BA%E6%99%AF%E4%B8%AD%E7%9A%84%E5%BA%94%E7%94%A8%20%282%29.png&originHeight=275&originWidth=739&originalType=binary&ratio=1&rotation=0&showTitle=false&size=28973&status=done&style=none&taskId=uf076d769-702f-4f4f-9ebe-15b9fb3bf3c&title=)
​

​

# 总结
在整个 OAuth2 协议中，最关键的问题就是**如何获取客户端授权**。
​

就目前主流的微服架构来说，当发起 HTTP 请求时，关注的是如何通过 HTTP 协议透明而高效地传递令牌，此时授权码模式下通过回调地址进行授权管理的方式就不是很实用，密码模式反而更加简洁高效。
​

> OAuth2 协议中所具备的四大角色以及四种授权模式吗

​

​

