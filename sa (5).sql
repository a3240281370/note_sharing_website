-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- 主機： 127.0.0.1
-- 產生時間： 2023-12-27 12:24:46
-- 伺服器版本： 10.4.28-MariaDB
-- PHP 版本： 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `sa`
--

DELIMITER $$
--
-- 程序
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `CreateAccount` (IN `Account` VARCHAR(20), IN `Name` VARCHAR(20), IN `Password` VARCHAR(20))   BEGIN
	insert into User (`Account`,`Name`,`Password`,`Lock`)
    values (Account,Name,Password,0);
    
    select * from User
    where User.Account=Account;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `CreateComment` (IN `Content` VARCHAR(50), IN `Account` VARCHAR(20), IN `NoteID` VARCHAR(50))   BEGIN
	declare CommentID varchar(50);
    SELECT MD5(CONCAT(sysdate(6), RAND()))
    into CommentID;
    
    insert into Comment (`CommentID`,`Content`,`Time`,`Account`,`NoteID`)
    values (CommentID,Content,now(),Account,NoteID);
    
    update Notification
    set status=1
    where Notification.NoteID=NoteID;
    
    select * 
    from (Comment
          inner join User
          on Comment.Account=User.Account)
    where Comment.CommentID=CommentID;
    
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `CreateNote` (`NoteName` VARCHAR(20), `NoteDescription` VARCHAR(100), `Tag` VARCHAR(20), `Public` INT, `Account` VARCHAR(20))   BEGIN
	declare NoteID varchar(50);
    SELECT MD5(CONCAT(sysdate(6), RAND()))
    into NoteID;

	insert into Note (`NoteID`,`NoteName`,`NoteDescription`,`UploadTime`,`Tag`,`Like`,`Public`,`Account`)
    values (NoteID,NoteName,NoteDescription,now(),Tag,0,Public,Account);
    
    insert into notification (`NoteID`,`status`)
    values (NoteID,0);
    
    select * from Note
    where Note.NoteID=NoteID;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteComment` (`CommentID` VARCHAR(50))   BEGIN

	delete from Comment
    where Comment.CommentID=CommentID;
    
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteNote` (`NoteID` VARCHAR(50))   BEGIN
	SET FOREIGN_KEY_CHECKS=0;
    
	delete from Pic
    where Pic.NoteID=NoteID;
    
    delete from Comment
    where Comment.NoteID=NoteID;
    
    delete from Like_tbl
	where Like_tbl.NoteID=NoteID;
    
    delete from Notification
    where Notification.NoteID=NoteID;
    
    delete from Note
    where Note.NoteID=NoteID;
    
    SET FOREIGN_KEY_CHECKS=1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteUser` (`Account` VARCHAR(20))   BEGIN
	SET FOREIGN_KEY_CHECKS=0;
    
    delete from Pic
    where Pic.NoteID
    in (
			select NoteID
            from Note
            where Note.Account=Account
    );
    
    delete from User
    where User.Account=Account;
    
    delete from Comment
    where Comment.Account=Account;
    
    delete from Comment
    where Comment.NoteID
    in (
			select NoteID
            from Note
            where Note.Account=Account
    );
    
    update Note
    set Note.Like=Note.Like-1
    where Note.NoteID
    in(
		select NoteID
        from Like_tbl
        where Like_tbl.Account=Account
    );
    
    delete from Like_tbl
    where Like_tbl.Account=Account;
    
	delete from Like_tbl
	where Like_tbl.NoteID
    in (
			select NoteID
            from Note
            where Note.Account=Account
    );
    
    delete from Notification
    where Notification.NoteID
    in (
			select NoteID
            from Note
            where Note.Account=Account
    );
    
	delete from Note
    where Note.Account=Account;
    
    SET FOREIGN_KEY_CHECKS=1;
    
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `DisLike` (`Account` VARCHAR(20), `NoteID` VARCHAR(50))   BEGIN
	delete from Like_tbl
    where Like_tbl.Account=Account and Like_tbl.NoteID=NoteID;
    
    update Note
    set Note.Like=Note.Like-1
    where Note.NoteID=NoteID;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetAllNote` ()   BEGIN
	select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
    from (Note
          inner join User
          on Note.Account=User.Account)
    where Note.Public=1
    order by UploadTime desc;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetAllUser` ()   BEGIN
	select User.Account,User.Name,User.Password,User.Lock
    from User;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetComment` (IN `NoteID` VARCHAR(50))   BEGIN
	select Comment.CommentID,Comment.Content,Comment.Time,User.Name
    from (Comment
         inner join User
         on Comment.Account=User.Account)
    where Comment.NoteID=NoteID
    order by Comment.Time desc;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetNewStatus` (`Account` VARCHAR(20))   BEGIN
	select count(*)
    from(
		select noti.status
		from User U
		join Note N
		join notification noti
		where U.Account=Account and N.Account=Account and N.NoteID= noti.NoteID and status=1
    )NotifyTBL;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetNoteInfo` (IN `NoteID` VARCHAR(50))   BEGIN
    select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
    from (Note
          inner join User
          on Note.Account=User.Account)
    where Note.NoteID=NoteID;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetNotification` (`Account` VARCHAR(20))   BEGIN
	select N.NoteID,N.NoteName
	from User U
		join Note N
		join notification noti
	where U.Account=Account and N.Account=Account and N.NoteID= noti.NoteID and noti.status=1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetOwnNote` (IN `Account` VARCHAR(20))   BEGIN
    select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
    from (Note
          inner join User
          on Note.Account=User.Account)
    where Note.Account=Account;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetPic` (IN `NoteID` VARCHAR(50))   BEGIN
	select *
    from Pic 
    where Pic.NoteID = NoteID
    order by Sequence;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `Like` (`Account` VARCHAR(20), `NoteID` VARCHAR(50))   BEGIN
	insert into Like_tbl (`Account`,`NoteID`)
    values (Account,NoteID);
    
    update Note
    set Note.Like=Note.Like+1
    where Note.NoteID=NoteID;
    
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchAccount` (`word` VARCHAR(20))   BEGIN
	select *
    from User
    where Name Like concat('%',word,'%');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchByLike` (IN `word` VARCHAR(20))   BEGIN
	select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
    from (Note
          inner join User
          on Note.Account=User.Account)
    where NoteName like concat('%',word,'%') or Note.NoteDescription like concat('%',word,'%')  or Note.Tag like concat('%',word,'%') or Note.Account like concat('%',word,'%') and Note.Public=1
    order by Note.Like desc;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchByWord` (IN `word` VARCHAR(20))   BEGIN
	select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
    from (Note
          inner join User
          on Note.Account=User.Account)
    where (NoteName like concat('%',word,'%') or Note.NoteDescription like concat('%',word,'%')  or Note.Tag like concat('%',word,'%') or Note.Account like concat('%',word,'%')) and Note.Public=1
    order by UploadTime desc;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchByYear` (IN `word` VARCHAR(20), IN `year` INT)   BEGIN
	select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
    from (Note
          inner join User
          on Note.Account=User.Account)
    where (NoteName like concat('%',word,'%') or Note.NoteDescription like concat('%',word,'%')  or Note.Tag like concat('%',word,'%') or Note.Account like concat('%',word,'%')) 
    and (UploadTime >=concat(year,'-01-01 00:00:00') and UploadTime <=concat(year,'-12-31 11:59:59')) and Note.Public=1
    order by UploadTime desc;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchNote` (IN `word` VARCHAR(20))   BEGIN
	select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
    from (Note
	  inner join User
	  on Note.Account = User.Account)
    where NoteName like concat('%',word,'%') or NoteDescription like concat('%',word,'%') or Tag like concat('%',word,'%') or User.Name like concat('%',word,'%') and Note.Public=1
    order by Note.UploadTime desc;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchNoteByLike` ()   BEGIN
	select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
    from (Note
          inner join User
          on Note.Account=User.Account)
    where Note.Public=1
    order by Note.Like desc;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchNoteByYear` (IN `year` INT)   BEGIN
	select Note.NoteID,Note.NoteName,Note.NoteDescription,Note.UploadTime,Note.Tag,Note.Like,Note.Public,Note.Account,User.Name
	from (Note
          inner join User
          on Note.Account=User.Account)
	where UploadTime >=concat(year,'-01-01 00:00:00') and UploadTime <=concat(year,'-12-31 11:59:59') and Note.Public=1
    order by UploadTime desc;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `UpdateAccount` (IN `Account` VARCHAR(20), IN `Name` VARCHAR(20), IN `Password` VARCHAR(20))   BEGIN
    
    UPDATE User
	SET `Name` = Name,
        `Password` = Password
	WHERE User.Account = Account;

	select * from User
    where User.Account=Account;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `UpdateNote` (IN `NoteID` VARCHAR(50), IN `NoteName` VARCHAR(20), IN `NoteDescription` VARCHAR(100), IN `Tag` VARCHAR(20), IN `Public` INT)   BEGIN
	update Note
	set `NoteName` = NoteName,
		`NoteDescription` = NoteDescription,
        `Tag` = Tag,
        `Public` = Public,
        `UploadTime` = now()
	where Note.NoteID = NoteID;
    
    select * from Note
    where Note.NoteID=NoteID;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `UpdateNotification` (`Account` VARCHAR(20))   BEGIN
	update Notification
    set status=0
    where Notification.NoteID
    in(
		select NoteID
        from Note
        where Note.Account=Account
    );
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- 資料表結構 `admin`
--

CREATE TABLE `admin` (
  `Account` varchar(20) NOT NULL,
  `Password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `admin`
--

INSERT INTO `admin` (`Account`, `Password`) VALUES
('ad1', 'ad1'),
('admin1', 'ad1'),
('admin2', 'admin2'),
('admin3', 'admin3'),
('qq', 'qq');

-- --------------------------------------------------------

--
-- 資料表結構 `comment`
--

CREATE TABLE `comment` (
  `CommentID` varchar(50) NOT NULL,
  `Content` varchar(50) NOT NULL,
  `Time` datetime NOT NULL,
  `Account` varchar(20) NOT NULL,
  `NoteID` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `comment`
--

INSERT INTO `comment` (`CommentID`, `Content`, `Time`, `Account`, `NoteID`) VALUES
('1', '好ㄟ', '2022-12-12 11:05:12', 'a', '2'),
('2', '謝謝', '2022-12-14 11:05:12', 'b', '2'),
('37b6e2a5a233d9a750033901ed504d2a', '企概到底在公三小', '2023-12-10 11:05:12', 'd', '2'),
('3f34679059797153bc286eb9a3f0cd1c', 'aa', '2023-12-24 00:49:30', 'a', '2'),
('4f5222741ba48b79a0b3f3dedb4fe6ea', '讚讚', '2023-12-09 11:05:12', 'b', '2'),
('55c0338c9ca9047ad23cbbcf5dc06235', '又涼又甜', '2023-12-17 17:25:08', 'a', 'd79e55d02186f10505760d816410d3cf'),
('5893507da51bf83c9743ca6db6157a62', 'QQQ', '2023-12-26 23:18:12', 'a', '77f4f0869851bfe5f7523709eda737e7'),
('5b52f418a3bfcea95c5c35620f7aab36', '看不見得手', '2023-12-11 10:58:59', 'a', '50a0dbafdb4456ef1df0c1eb3eb36f76'),
('966d2ce0f1a81bc33c53904e5d6e2055', 'qw', '2023-12-24 00:40:58', 'a', '2'),
('dc6f14beb832fd51b8f96ad7210d2bf4', 'www', '2023-12-26 23:32:28', 'a', '77f4f0869851bfe5f7523709eda737e7'),
('df99496344a2f5892049970840f49265', '恩頤好讚', '2023-12-11 00:00:00', 'b', '2'),
('e16bfcac4de2edac38dac7b66807fccb', '大神', '2023-12-11 10:58:17', 'b', '50a0dbafdb4456ef1df0c1eb3eb36f76');

-- --------------------------------------------------------

--
-- 資料表結構 `like_tbl`
--

CREATE TABLE `like_tbl` (
  `Account` varchar(50) NOT NULL,
  `NoteID` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `like_tbl`
--

INSERT INTO `like_tbl` (`Account`, `NoteID`) VALUES
('a', '2'),
('a', 'd79e55d02186f10505760d816410d3cf'),
('b', '1'),
('d', '2');

-- --------------------------------------------------------

--
-- 資料表結構 `note`
--

CREATE TABLE `note` (
  `NoteID` varchar(50) NOT NULL,
  `NoteName` varchar(20) NOT NULL,
  `NoteDescription` varchar(100) NOT NULL,
  `UploadTime` datetime NOT NULL,
  `Tag` varchar(20) NOT NULL,
  `Like` int(11) NOT NULL,
  `Public` int(11) NOT NULL,
  `Account` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `note`
--

INSERT INTO `note` (`NoteID`, `NoteName`, `NoteDescription`, `UploadTime`, `Tag`, `Like`, `Public`, `Account`) VALUES
('1', '計概', '計概考古', '2023-12-11 11:08:20', '計算機概論', 108, 0, 'a'),
('2', '企概', '企業概論', '2022-12-23 11:05:12', '企業概論', 58, 0, 'b'),
('346380203a81b806954107a49af54825', 'ob', 'qq', '2023-12-24 17:53:00', 'qq', 0, 1, 'qq'),
('50a0dbafdb4456ef1df0c1eb3eb36f76', 'economic', '2上經濟學', '2022-12-11 10:56:34', '經濟學', 0, 1, 'd'),
('517a12738b3ae54ad0eeaeff2d0ddca4', 'a', 'a', '2023-12-11 21:07:44', 'a', 0, 1, 'a'),
('77f4f0869851bfe5f7523709eda737e7', 'OR note', 'sue sue', '2023-12-25 10:20:17', '#作業研究', 0, 1, 'a'),
('7ef7f5e58bccd6a72003b85e9d133548', 'test', 'test', '2023-12-17 16:54:35', 't', 0, 1, 'qq'),
('bf8fd0e09ea8c9df0f700e8218aad188', 'SA', 'SA so hard', '2023-12-25 10:13:23', '#系統分析與設計', 0, 1, 'a'),
('d79e55d02186f10505760d816410d3cf', '網頁程設', 'html.css.js.php', '2022-12-10 11:05:12', '網頁程式設計', 1, 1, 'b'),
('f58cfba94714fe76b3f34b9a58634f4f', 'economic', '2下經濟學', '2023-12-11 11:01:35', '經濟學', 0, 1, 'd'),
('fb244185591fa079c9f9b7ffc06358cc', '企資通', '計算機網路', '2023-12-07 11:05:12', '企資通', 0, 1, 'a');

-- --------------------------------------------------------

--
-- 資料表結構 `notification`
--

CREATE TABLE `notification` (
  `NoteID` varchar(50) NOT NULL,
  `status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `notification`
--

INSERT INTO `notification` (`NoteID`, `status`) VALUES
('1', 0),
('2', 1),
('346380203a81b806954107a49af54825', 0),
('50a0dbafdb4456ef1df0c1eb3eb36f76', 1),
('517a12738b3ae54ad0eeaeff2d0ddca4', 0),
('77f4f0869851bfe5f7523709eda737e7', 1),
('7ef7f5e58bccd6a72003b85e9d133548', 1),
('bf8fd0e09ea8c9df0f700e8218aad188', 0),
('d79e55d02186f10505760d816410d3cf', 1),
('f58cfba94714fe76b3f34b9a58634f4f', 1),
('fb244185591fa079c9f9b7ffc06358cc', 0);

-- --------------------------------------------------------

--
-- 資料表結構 `pic`
--

CREATE TABLE `pic` (
  `PicID` varchar(50) NOT NULL,
  `PicURL` varchar(1000) NOT NULL,
  `NoteID` varchar(50) NOT NULL,
  `Sequence` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `pic`
--

INSERT INTO `pic` (`PicID`, `PicURL`, `NoteID`, `Sequence`) VALUES
('1', './a/1', '1', 1),
('1e1d6321756da384815005175cb29f82', './b', '2', 4),
('2', './a/2', '1', 2),
('2097ffcc074407171c33f558f52a1f3c', '\\SA\\static\\a-2.png', '77f4f0869851bfe5f7523709eda737e7', 3),
('3', './a/3', '1', 3),
('4', './b/1', '2', 1),
('40cdc061db05a8597a8be06bd8e467c4', '\\SA\\static\\a-1.png', '77f4f0869851bfe5f7523709eda737e7', 2),
('5', './b/2', '2', 2),
('55d84d8374a589bacc175d1f2e962457', '\\SA\\static\\Screenshot 2023-12-17 202641.png', 'bf8fd0e09ea8c9df0f700e8218aad188', 3),
('6', './b/3', '2', 3),
('7d20914e4e8eef9944ec56ac7fbf9d77', '\\SA\\static\\Screenshot 2023-12-01 204255.png', 'bf8fd0e09ea8c9df0f700e8218aad188', 1),
('906824f53dc798e6b86b91b4fdea56db', '\\SA\\static\\lebron21shoe.png', '346380203a81b806954107a49af54825', 3),
('91b69fba98715882a642ff9df0dc0297', '\\SA\\static\\ob.png', '346380203a81b806954107a49af54825', 1),
('92f3eca69782afb06395c22a83d5183e', '\\SA\\static\\Screenshot 2023-12-01 204735.png', 'bf8fd0e09ea8c9df0f700e8218aad188', 2),
('ae4137ffc425ad9e47d50c2967979487', '\\SA\\static\\a-2.png', '77f4f0869851bfe5f7523709eda737e7', 1),
('e30bd3ba3296acef5f2a54aff189306d', '\\SA\\static\\lebron21.png', '346380203a81b806954107a49af54825', 2);

-- --------------------------------------------------------

--
-- 資料表結構 `user`
--

CREATE TABLE `user` (
  `Account` varchar(20) NOT NULL,
  `Name` varchar(20) NOT NULL,
  `Password` varchar(20) NOT NULL,
  `Lock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `user`
--

INSERT INTO `user` (`Account`, `Name`, `Password`, `Lock`) VALUES
('a', 'aa', 'a', 1),
('aa', 'a', 'a', 0),
('aaa', 'aaa', 'aaa', 0),
('b', 'bb', 'b', 0),
('d', 'dd', 'd', 1),
('q', 'q', 'q', 0),
('qq', 'qq', 'qqq', 0);

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`Account`);

--
-- 資料表索引 `comment`
--
ALTER TABLE `comment`
  ADD PRIMARY KEY (`CommentID`,`NoteID`,`Account`),
  ADD KEY `fk_Comment_Note1_idx` (`NoteID`),
  ADD KEY `fk_Comment_User1_idx` (`Account`);

--
-- 資料表索引 `like_tbl`
--
ALTER TABLE `like_tbl`
  ADD PRIMARY KEY (`Account`,`NoteID`),
  ADD KEY `fk_tbl_User_Note_Note1_idx` (`NoteID`);

--
-- 資料表索引 `note`
--
ALTER TABLE `note`
  ADD PRIMARY KEY (`NoteID`,`Account`),
  ADD KEY `fk_Note_User1_idx` (`Account`);

--
-- 資料表索引 `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`NoteID`);

--
-- 資料表索引 `pic`
--
ALTER TABLE `pic`
  ADD PRIMARY KEY (`PicID`,`NoteID`),
  ADD KEY `fk_Pic_Note1_idx` (`NoteID`);

--
-- 資料表索引 `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`Account`);

--
-- 已傾印資料表的限制式
--

--
-- 資料表的限制式 `comment`
--
ALTER TABLE `comment`
  ADD CONSTRAINT `fk_Comment_Note1` FOREIGN KEY (`NoteID`) REFERENCES `note` (`NoteID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Comment_User1` FOREIGN KEY (`Account`) REFERENCES `user` (`Account`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- 資料表的限制式 `like_tbl`
--
ALTER TABLE `like_tbl`
  ADD CONSTRAINT `fk_tbl_User_Note_Note1` FOREIGN KEY (`NoteID`) REFERENCES `note` (`NoteID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_tbl_User_Note_User1` FOREIGN KEY (`Account`) REFERENCES `user` (`Account`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- 資料表的限制式 `note`
--
ALTER TABLE `note`
  ADD CONSTRAINT `fk_Note_User1` FOREIGN KEY (`Account`) REFERENCES `user` (`Account`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- 資料表的限制式 `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `fk_Note_Notification` FOREIGN KEY (`NoteID`) REFERENCES `note` (`NoteID`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- 資料表的限制式 `pic`
--
ALTER TABLE `pic`
  ADD CONSTRAINT `fk_Pic_Note1` FOREIGN KEY (`NoteID`) REFERENCES `note` (`NoteID`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
