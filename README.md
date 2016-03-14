# DragGridView
拖拽排序GridView,支持本地记录,对代码进行了封装，可直接使用。

## 稍后会对GrapGridView进行再次修改
## 稍后会对GrapGridView进行再次修改
## 稍后会对GrapGridView进行再次修改


ps:对 http://blog.csdn.net/vipzjyno1/article/details/25005851　
的代码进行了优化和封装，后续还会继续优化和封装。

# 若有bug,请提issue

# 1.0.0版本　目录结构及用法
 1. 目录结构
  　- base 
  　      - BaseDragAdapter 封装了一些可能用到的方法
         - BaseItem 数据实体Bean的基类，包含　toJson 和　fromJson
    - bean 
         - 实体类
    - draggridview
         - DragAdapter 继承自BaseDragAdapter
         - DragGridView 这个view目前还不好，有待优化
    - model
         - ProvinceModel 用于请求网络数据　或者　取本地数据
    - tools
         - Constant 一些常量字符串
         - ListToJson　将list转化为Json
         - Util 用于dp 和　px 之间转化
 2. 用法
    - Adapter　继承BaseDragAdapter
    - bean 继承　BaseUtem
    - 修改对应的model 即可
    - 若想修改 item　背景,请修改item_bg.xml
    - 其他　根据需求对代码进行修改

