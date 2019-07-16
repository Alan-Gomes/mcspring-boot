# Spring Boot Spigot Starter

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Alan-Gomes/mcspring-boot/fork)
[![Maven Central](https://img.shields.io/maven-central/v/dev.alangomes/spigot-spring-boot-starter.svg)](https://search.maven.org/#artifactdetails%7Cdev.alangomes%7Cspigot-spring-boot-starter%7C0.20.4%7Cjar)
[![License](https://img.shields.io/github/license/Alan-Gomes/mcspring-boot.svg?style=popout)](https://github.com/Alan-Gomes/mcspring-boot/blob/master/LICENSE)
[![Coverage Status](https://img.shields.io/coveralls/github/Alan-Gomes/mcspring-boot/master.svg)](https://coveralls.io/github/Alan-Gomes/mcspring-boot?branch=master)
[![GitHub Issues](https://img.shields.io/github/issues/Alan-Gomes/mcspring-boot.svg)](https://github.com/Alan-Gomes/mcspring-boot/issues)
[![CircleCI Status](https://img.shields.io/circleci/project/github/Alan-Gomes/mcspring-boot/master.svg)](https://circleci.com/gh/Alan-Gomes/mcspring-boot)

> A Spring boot starter for Bukkit/Spigot/PaperSpigot plugins

## Features

- Easy setup
- Full [Picocli](http://picocli.info/) `@Command` support (thanks to [picocli-spring-boot-starter](https://github.com/kakawait/picocli-spring-boot-starter)) 
- Secure calls with `@Authorize`
- Automatic `Listener` registration
- Session system
- (Optional) Support for RxJava event listeners
- Designed for testability
- Full Spring's dependency injection support
- Easier Bukkit main thread synchronization via `@Synchronize`
- Support Spring scheduler on the bukkit main thread (`@Scheduled`)

Check the [wiki](https://github.com/Alan-Gomes/mcspring-boot/wiki/Getting-started) for usage instructions :) 