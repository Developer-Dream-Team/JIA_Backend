# Job Interview Application

Help job seekers improve their interview skills through realistic simulations and a feedback system. This platform
leverages advanced AI technology to identify improvement areas, track progress, and offer customized coaching to prepare
users for success.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The Job Interview Application assists job seekers in enhancing their interview skills through AI-driven simulations and
personalized feedback. It provides a comprehensive platform for practicing and refining interview techniques.

## Features

- Realistic interview simulations
- AI-based feedback and improvement suggestions
- Progress tracking and performance analytics

## Technologies

- Java
- Spring Boot
- MongoDB
- Spring Security
- JWT (JSON Web Tokens)
- Other relevant libraries and tools

## Setup

To set up the project locally, follow these steps:

```bash
# Clone the repository
git clone https://github.com/Developer-Dream-Team/JIA_Backend.git

# Navigate to the project directory
cd JIA_Backend/

# Install dependencies
mvn clean install

# Set up environment variables
export MONGODB_URI="mongodb://localhost:27017/mydatabase"
export MONGODB_DATABASE="mydatabase"

# Run the application
mvn spring-boot:run
