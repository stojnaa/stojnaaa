-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: ps2
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `cart_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `total_price` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `uq_cart_user` (`user_id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user_ps2` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ck_cart_total_nonneg` CHECK ((`total_price` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,1,0),(2,2,4500),(3,3,0),(4,6,1117.5),(5,9,0);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `cart_id` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`cart_id`,`item_id`),
  KEY `idx_cart_item_item` (`item_id`),
  CONSTRAINT `fk_cart_item_cart` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cart_item_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_cart_item_qty_pos` CHECK ((`quantity` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES (1,3,1),(1,4,2),(2,1,1),(2,5,1),(4,6,1),(4,7,1);
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `parent_category_id` int DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uq_category_name_parent` (`name`,`parent_category_id`),
  KEY `fk_category_parent` (`parent_category_id`),
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (4,'Books',NULL),(1,'Electronics',NULL),(6,'elektronika',NULL),(5,'iqos',NULL),(3,'Laptops',1),(2,'Phones',1),(8,'telegoni',7),(7,'terea',NULL);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` text,
  `price` double NOT NULL,
  `discount_pct` double NOT NULL DEFAULT '0',
  `category_id` int NOT NULL,
  `seller_user_id` int NOT NULL,
  PRIMARY KEY (`item_id`),
  KEY `idx_item_category` (`category_id`),
  KEY `idx_item_seller` (`seller_user_id`),
  CONSTRAINT `fk_item_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_item_seller` FOREIGN KEY (`seller_user_id`) REFERENCES `user_ps2` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_item_discount_range` CHECK (((`discount_pct` >= 0) and (`discount_pct` <= 100))),
  CONSTRAINT `ck_item_price_nonneg` CHECK ((`price` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'iPhone 12','Used, good condition',450,10,2,2),(2,'ThinkPad T14','Business laptop',900,0,3,4),(3,'Clean Code','Programming book',35,5,4,1),(4,'USB-C Cable','1m cable',8.5,0,1,3),(5,'slusalice','bluetooth',5000,10,6,1),(6,'iphone 13','polovan, ocuvan',650,5,8,6),(7,'samsung','nov',500,0,8,6);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_ps2`
--

DROP TABLE IF EXISTS `user_ps2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_ps2` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uq_user_ps2_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_ps2`
--

LOCK TABLES `user_ps2` WRITE;
/*!40000 ALTER TABLE `user_ps2` DISABLE KEYS */;
INSERT INTO `user_ps2` VALUES (1,'admin'),(9,'ahaa'),(6,'anaa'),(7,'idees'),(3,'ivana'),(5,'jelena'),(4,'marko'),(2,'milan'),(10,'tanja');
/*!40000 ALTER TABLE `user_ps2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist` (
  `wishlist_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`wishlist_id`),
  UNIQUE KEY `uq_wishlist_user` (`user_id`),
  CONSTRAINT `fk_wishlist_user` FOREIGN KEY (`user_id`) REFERENCES `user_ps2` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist`
--

LOCK TABLES `wishlist` WRITE;
/*!40000 ALTER TABLE `wishlist` DISABLE KEYS */;
INSERT INTO `wishlist` VALUES (1,1,'2026-02-26 07:30:15'),(2,3,'2026-02-26 07:30:15'),(3,2,'2026-02-26 14:37:01'),(4,6,'2026-02-28 06:57:50');
/*!40000 ALTER TABLE `wishlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist_item`
--

DROP TABLE IF EXISTS `wishlist_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist_item` (
  `wishlist_id` int NOT NULL,
  `item_id` int NOT NULL,
  `added_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`wishlist_id`,`item_id`),
  KEY `idx_wishlist_item_item` (`item_id`),
  CONSTRAINT `fk_wishlist_item_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_wishlist_item_wishlist` FOREIGN KEY (`wishlist_id`) REFERENCES `wishlist` (`wishlist_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist_item`
--

LOCK TABLES `wishlist_item` WRITE;
/*!40000 ALTER TABLE `wishlist_item` DISABLE KEYS */;
INSERT INTO `wishlist_item` VALUES (1,1,'2026-02-26 07:30:15'),(1,2,'2026-02-26 07:30:15'),(2,3,'2026-02-26 07:30:15');
/*!40000 ALTER TABLE `wishlist_item` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-28 19:45:02
