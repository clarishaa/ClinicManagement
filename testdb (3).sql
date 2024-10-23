-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 23, 2024 at 03:03 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `testdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `appointments`
--

CREATE TABLE `appointments` (
  `id` int NOT NULL,
  `patient_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `service` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `billing`
--

CREATE TABLE `billing` (
  `id` int NOT NULL,
  `treatment_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `billing_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('paid','pending','canceled') NOT NULL DEFAULT 'pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `billing`
--

INSERT INTO `billing` (`id`, `treatment_id`, `amount`, `billing_date`, `status`) VALUES
(1, 13, 200.00, '2024-10-22 16:00:00', 'paid'),
(2, 15, 100.00, '2024-10-23 14:04:22', 'pending'),
(3, 16, 200.00, '2024-10-23 14:13:04', 'pending'),
(4, 17, 400.00, '2024-10-23 14:28:06', 'pending');

-- --------------------------------------------------------

--
-- Table structure for table `bills`
--

CREATE TABLE `bills` (
  `id` int NOT NULL,
  `appointment_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `bill_date` date NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

CREATE TABLE `inventory` (
  `item_id` int NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `inventory`
--

INSERT INTO `inventory` (`item_id`, `item_name`, `quantity`, `unit_price`) VALUES
(1, 'Chair', 888, 888.00),
(4, 'gfcdx', 22, 233.00);

-- --------------------------------------------------------

--
-- Table structure for table `treatments`
--

CREATE TABLE `treatments` (
  `id` int NOT NULL,
  `user_id` int NOT NULL,
  `treatment_type` varchar(100) NOT NULL,
  `treatment_date` date NOT NULL,
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `treatments`
--

INSERT INTO `treatments` (`id`, `user_id`, `treatment_type`, `treatment_date`, `notes`, `created_at`) VALUES
(2, 10, 'Teeth Cleaning', '2024-10-23', 'cvbnm,', '2024-10-23 10:42:36'),
(4, 10, 'Checkup', '2024-10-23', 'fghjkl', '2024-10-23 10:47:07'),
(5, 10, 'Checkup', '2024-10-23', 'fghjk', '2024-10-23 10:53:02'),
(7, 10, 'Checkup', '2024-10-23', 'dcfvgbhnjmk,', '2024-10-23 11:03:41'),
(8, 10, 'Teeth Cleaning', '2024-10-23', 'cvbnm,', '2024-10-23 11:11:29'),
(9, 10, 'Teeth Cleaning', '2024-10-23', 'cvbnm,', '2024-10-23 11:11:37'),
(10, 13, 'Treatment C', '2024-10-23', 'hgfd', '2024-10-23 12:08:23'),
(12, 10, 'vbnm,', '2024-10-23', 'nbbbbbbb', '2024-10-23 12:46:05'),
(13, 10, 'Cleaning', '2024-10-23', 'cvbnm', '2024-10-23 13:55:42'),
(14, 10, 'Filling', '2024-10-23', 'bnmkl', '2024-10-23 14:03:28'),
(15, 10, 'Consultation', '2024-10-23', 'vbnjmk,', '2024-10-23 14:04:22'),
(16, 13, 'Cleaning', '2024-10-23', 'xz', '2024-10-23 14:13:04'),
(17, 10, 'Extraction', '2024-10-23', 'cvbnm,', '2024-10-23 14:28:06');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_type` enum('admin','patient') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'patient',
  `first_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `last_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `gender` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `birthdate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `user_type`, `first_name`, `last_name`, `gender`, `birthdate`) VALUES
(5, 'admin', 'admin@gmail.com', 'admin', 'admin', NULL, NULL, NULL, NULL),
(10, NULL, NULL, NULL, 'patient', 'Clarish', 'Jabonillo', 'Female', '2025-10-23'),
(13, NULL, NULL, NULL, 'patient', 'ccc', 'nnnnn', 'Male', '2024-10-23'),
(14, NULL, NULL, NULL, 'patient', 'Clarish', 'Oli', 'Female', '2002-08-29');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `billing`
--
ALTER TABLE `billing`
  ADD PRIMARY KEY (`id`),
  ADD KEY `treatment_id` (`treatment_id`);

--
-- Indexes for table `bills`
--
ALTER TABLE `bills`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `inventory`
--
ALTER TABLE `inventory`
  ADD PRIMARY KEY (`item_id`);

--
-- Indexes for table `treatments`
--
ALTER TABLE `treatments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `appointments`
--
ALTER TABLE `appointments`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `billing`
--
ALTER TABLE `billing`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `bills`
--
ALTER TABLE `bills`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `inventory`
--
ALTER TABLE `inventory`
  MODIFY `item_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `treatments`
--
ALTER TABLE `treatments`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `billing`
--
ALTER TABLE `billing`
  ADD CONSTRAINT `billing_ibfk_1` FOREIGN KEY (`treatment_id`) REFERENCES `treatments` (`id`);

--
-- Constraints for table `treatments`
--
ALTER TABLE `treatments`
  ADD CONSTRAINT `treatments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
