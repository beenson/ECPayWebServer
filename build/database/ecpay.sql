-- phpMyAdmin SQL Dump
-- version 5.0.3
-- https://www.phpmyadmin.net/
--
-- 主機： 127.0.0.1
-- 產生時間： 2021-05-24 14:52:25
-- 伺服器版本： 10.4.14-MariaDB
-- PHP 版本： 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `ecpay`
--

-- --------------------------------------------------------

--
-- 資料表結構 `category`
--

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `priority` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 傾印資料表的資料 `category`
--

INSERT INTO `category` (`id`, `name`, `priority`) VALUES
(2, '3C', 0),
(3, 'Apple', 0),
(4, '飲料', 1),
(5, '書', 2),
(7, 'testeee', 0);

-- --------------------------------------------------------

--
-- 資料表結構 `orderitems`
--

CREATE TABLE `orderitems` (
  `id` int(11) NOT NULL,
  `orderId` int(11) NOT NULL COMMENT '訂單編號',
  `productId` int(11) NOT NULL COMMENT '商品編號',
  `amount` int(11) DEFAULT NULL COMMENT '購買數量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 資料表結構 `orderpayments`
--

CREATE TABLE `orderpayments` (
  `id` int(11) NOT NULL,
  `orderId` int(11) NOT NULL COMMENT '訂單編號',
  `type` int(11) DEFAULT NULL COMMENT '付款方式',
  `status` int(11) DEFAULT NULL COMMENT '付款狀態',
  `orderNumber` varchar(255) DEFAULT NULL COMMENT '綠界編號',
  `bank` varchar(255) DEFAULT NULL COMMENT '銀行代號',
  `code` varchar(255) DEFAULT NULL COMMENT '銀行帳號|超商繳費代碼'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 資料表結構 `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `userId` int(11) NOT NULL COMMENT '使用者編號',
  `price` int(10) DEFAULT NULL COMMENT '總價',
  `status` int(10) DEFAULT NULL COMMENT '訂單狀態',
  `createAt` datetime DEFAULT NULL COMMENT '建立時間'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

--
-- 傾印資料表的資料 `orders`
--

INSERT INTO `orders` (`id`, `userId`, `price`, `status`, `createAt`) VALUES
(3, 7, 1000, 0, '2021-05-09 00:00:00');

-- --------------------------------------------------------

--
-- 資料表結構 `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '商品名稱',
  `price` int(10) DEFAULT NULL COMMENT '價格',
  `desciption` varchar(255) DEFAULT NULL COMMENT '描述',
  `sellAmount` int(255) DEFAULT NULL COMMENT '售出數量',
  `storageAmount` int(255) DEFAULT NULL COMMENT '庫存數量',
  `onSell` int(255) DEFAULT NULL COMMENT '是否上架',
  `photo` varchar(255) DEFAULT NULL COMMENT '圖片網址',
  `categoryId` int(11) NOT NULL COMMENT '分類ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

--
-- 傾印資料表的資料 `products`
--

INSERT INTO `products` (`id`, `name`, `price`, `desciption`, `sellAmount`, `storageAmount`, `onSell`, `photo`, `categoryId`) VALUES
(7, 'newProduct_115', 163, 'description', 7, 15, 1, 'https://picsum.photos/id/364/800/800', 2),
(8, 'newProduct_275', 309, 'description', 5, 16, 1, NULL, 2),
(9, 'nP_HAHA', 100, 'haha', 200, 2000, 1, 'https://picsum.photos/id/109/800/800', 3),
(10, 'nP_HA10', 100, 'haha', 200, 2000, 1, 'https://picsum.photos/id/109/800/800', 4),
(11, 'nP_HAHA', 100, 'haha', 200, 2000, 1, 'https://picsum.photos/id/109/800/800', 2);

-- --------------------------------------------------------

--
-- 資料表結構 `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `admin` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

--
-- 傾印資料表的資料 `users`
--

INSERT INTO `users` (`id`, `admin`, `name`, `email`, `password`, `phone`) VALUES
(1, 1, 'name', 'email2', 'pass', 'phone'),
(7, 1, 'name', 'email2', 'pass', 'phone'),
(8, 1, 'name', 'email2', 'pass', 'phone');

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- 資料表索引 `orderitems`
--
ALTER TABLE `orderitems`
  ADD PRIMARY KEY (`id`,`orderId`,`productId`) USING BTREE,
  ADD KEY `orderid` (`orderId`) USING BTREE,
  ADD KEY `productId` (`productId`) USING BTREE;

--
-- 資料表索引 `orderpayments`
--
ALTER TABLE `orderpayments`
  ADD PRIMARY KEY (`id`,`orderId`) USING BTREE;

--
-- 資料表索引 `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`,`userId`) USING BTREE,
  ADD KEY `userid` (`userId`) USING BTREE,
  ADD KEY `id` (`id`) USING BTREE;

--
-- 資料表索引 `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `categoryId` (`categoryId`);

--
-- 資料表索引 `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 在傾印的資料表使用自動遞增(AUTO_INCREMENT)
--

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `category`
--
ALTER TABLE `category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `orderitems`
--
ALTER TABLE `orderitems`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `orderpayments`
--
ALTER TABLE `orderpayments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- 已傾印資料表的限制式
--

--
-- 資料表的限制式 `orderitems`
--
ALTER TABLE `orderitems`
  ADD CONSTRAINT `orderid` FOREIGN KEY (`orderId`) REFERENCES `orders` (`id`),
  ADD CONSTRAINT `productId` FOREIGN KEY (`productId`) REFERENCES `products` (`id`);

--
-- 資料表的限制式 `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `userid` FOREIGN KEY (`userId`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
