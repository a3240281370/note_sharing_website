# note_sharing_website

測試帳號密碼(需要我這邊的虛擬機有開啟才能測試)

```bash
帳號： test1@gmail.com
密碼： test123456
```

### 用戶功能
- [x] 登入
- [x] 登出
- [x] 註冊
- [x] 上傳筆記
- [x] 筆記排序（時間、按讚數）
- [x] 通知
- [x] 個人資訊
- [x] 留言、按讚

### 管理員功能
- [x] 新增管理員
- [x] 登出
- [x] 刪除、修改筆記
- [x] 刪除、封鎖用戶
- [x] 刪除管理員


## 畫面

![範例圖片 1](https://github.com/a3240281370/note_sharing_website/blob/main/example1.jpg)
![範例圖片 2](https://github.com/a3240281370/note_sharing_website/blob/main/example2.jpg)
![範例圖片 3]()

## 安裝

java SE 17
XAMPP 8.2.4
Apache Tomcat 9.0.82

## 必要操作
1. 請使用專案內的 server.xml 新增 Tomcat 的設定檔 (Server 路徑 >> conf >> server.xml)
   <Context debug="0" docBase="下載本專案後的絕對路徑" path="SA\static" reloadable="true"/>
2. java PicController @MultipartConfig 要修改成tomcat server底下的server path# note_sharing_website

### 開啟專案

eclipse將程式開啟
右鍵點擊資料夾並選取run on server

```bash
http://localhost:8090/SA/
```

## 聯絡作者

你可以透過以下方式與我聯絡

- [Instagram](https://www.instagram.com/hung_mmi/)

