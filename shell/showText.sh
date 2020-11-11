#!/bin/bash

# 是否使用kdialog
isKde=1
# 轮询时间（单位：秒）
sleeptime=10
#qt
kdialog="/usr/bin/kdialog"
#gtk
zenity="/usr/bin/zentity"

checkIsKde (){
    if [ -x $kdialog ]; then
        isKde=1;
    elif [ -x $zenity ]; then
        isKde=0
    else
        isKde=-1
    fi
}

checkIsKde
if [ $isKde -eq -1 ];then
    exit 0
fi
# isKde=0 # for test

if [ $isKde -eq 1 ];then
  kdialog --msgbox "$*"
else
  zenity --text-info "$*"
fi
