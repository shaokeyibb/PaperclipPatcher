# PaperclipPatcher

PaperclipPatcher 是一个 Java 应用程序（使用 Kotlin JVM 编写），可以帮助你更改 Paperclip 的 Vanilla 服务端下载地址到国内友好的镜像站点（默认为 MCBBS 的 BMCLAPI 镜像）

## 用法

1. 将 PaperclipPatcher.jar 放入包含一个或多个 Paperclip jar 的目录，请确保 PaperclipPatcher 和 Paperclip 在同一文件夹内
2. 运行 `java -jar PaperclipPatcher-1.0-SNAPSHOT-all.jar`
3. 等待 Patch 完成
4. 你将能看到 `patched_XXXXXX.jar` 文件生成
5. 使用这个文件开服即可

## 支持的版本

- 理论上支持全部版本的 Paperclip，甚至一些其他的 Paper 下游项目，如 Purpur
- 已经过测试的 Paperclip 版本对应的 Minecraft 版本有 `1.8, 1.12, 1.16, 1.18`

## 编译

1. 运行 `gradle build`
2. 编译后的文件位于 `build/libs/PaperclipPatcher-1.0-SNAPSHOT-all.jar`

## 开源

本软件是自由软件，你可以在遵循 [GPLv3](/LICENSE) 开源许可证的条件下对本软件进行修改、重新发布和/或分发。