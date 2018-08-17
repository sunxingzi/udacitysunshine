# udacitysunshine
将原先设置部分的功能代码修改成弹出自定义对话框的形式，美化了界面，同时相应的功能代码使用DialogFragment实现。(原先设置部分使用继承OnSharedPreferenceChangeListener的方式)



                                                   
                  ==ForecastAdapter==                 cursor                    ==MainActivity== 
                                                  
                                                                           ItemUri


                                                          ==DetailActivity==

MainActivity中使用两种后台服务来请求数据，一种定时服务，每隔3到4小时进行一次数据更新，另一种IntentService进行立即更新操作。数据请求在子线程中进行网络访问，解析数据，使用ContentProvider操作数据库，实现数据的存储和查询(删除旧数据，保存新数据)。

在MainActivity中使用CursorLoader，从ContentProvider异步加载数据，返回Cursor传递给ForecastAdapter进行数据展示。同理将ItemUri作为点击事件的数据传递给DetailActivity，在DetailActivity中使用传递过来的Uri在ContentProvider中查询展示相应子条目的的更多数据内容。
