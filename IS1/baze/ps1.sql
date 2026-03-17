-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: ps1
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
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `city` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_city_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (13,'beogr'),(1,'Beograd'),(6,'Čačak'),(14,'cokk'),(15,'eee'),(16,'kk'),(4,'Kragujevac'),(7,'Kraljevo'),(18,'ll'),(3,'Niš'),(11,'Nova varos'),(2,'Novi Sad'),(9,'priboj'),(10,'prijepolje'),(5,'Subotica'),(17,'uu'),(19,'www'),(8,'Zrenjanin');
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_role_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'ADMIN'),(2,'SELLER');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usr`
--

DROP TABLE IF EXISTS `usr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usr` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL,
  `first_name` varchar(64) DEFAULT NULL,
  `last_name` varchar(64) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `balance` bigint NOT NULL DEFAULT '0',
  `city_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_usr_username` (`username`),
  KEY `idx_usr_city` (`city_id`),
  CONSTRAINT `fk_usr_city` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usr`
--

LOCK TABLES `usr` WRITE;
/*!40000 ALTER TABLE `usr` DISABLE KEYS */;
INSERT INTO `usr` VALUES (1,'admin','admin123','Ana','Admin','Bulevar kralja Aleksandra 73',100000,1),(2,'milan','pass123','Milan','Jovanović','Narodnog fronta 12',25000,2),(3,'ivana','pass123','Ivana','Petrović','Nemanjina 5',15000,3),(4,'marko','pass123','Marko','Nikolić','Kneza Miloša 10',5000,4),(5,'jelena','pass123','Jelena','Stojanović','Cara Dušana 21',0,1),(6,'coki','123','cooki','ps','nemanjina',700,11),(7,'anaa','ana123','anaa','petrovic','haha',5000,11),(8,'idees','123','ides','ideeees','haha',8000,11),(9,'ahaa','haha','ahaa','ahaaa','jaja',2500,11),(10,'tanja','123','tanja','tanja','kaka',2000,9);
/*!40000 ALTER TABLE `usr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usr_role`
--

DROP TABLE IF EXISTS `usr_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usr_role` (
  `usr_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`usr_id`,`role_id`),
  KEY `idx_usr_role_role` (`role_id`),
  CONSTRAINT `fk_usr_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_usr_role_usr` FOREIGN KEY (`usr_id`) REFERENCES `usr` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usr_role`
--

LOCK TABLES `usr_role` WRITE;
/*!40000 ALTER TABLE `usr_role` DISABLE KEYS */;
INSERT INTO `usr_role` VALUES (1,1),(1,2),(2,2),(3,2),(4,2),(5,2);
/*!40000 ALTER TABLE `usr_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-28 19:43:33
