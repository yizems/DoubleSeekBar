# DoubleSeekBar

![效果图](https://i.loli.net/2018/01/10/5a55b852c4abe.gif)

## 使用方法

```xml

    <cn.yzl.android.doubleseekbar.DoubleSeekBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

```

## 注意事项


- 最低高度为80,wrapcontent 也为 80

- 该项目目前就我自己用,一些方法和属性没有抽取(没有时间呀-.-)

## 存在的问题


- 从gif图上也看到了,会重叠,目前还没有想好怎么解决 (文字重叠在1.0.2解决,下面的操作点还没有解决)


## 维护???


随缘吧(时间),如果有人要借鉴的话,还是希望你们能够自己重写下

## 更新日志


### 1.0.2

- 文字重叠解决(下面的mark点还没有解决)

![1.png](https://i.loli.net/2018/01/10/5a55e3afa24c7.png)
![2.png](https://i.loli.net/2018/01/10/5a55e3afafe64.png)

### 1.0.1

- 90-100的情况下 ,mark 点重叠的时候,触摸事件以左边的优先,其他情况 右边的优先


## 依赖

[![](https://jitpack.io/v/yizeliang/DoubleSeekBar.svg)](https://jitpack.io/#yizeliang/DoubleSeekBar)

```gradle

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
    	compile 'com.github.yizeliang:DoubleSeekBar:1.0.0'
    }

```


## License

    Copyright 2017 Yi Zeliang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



