MinFare
=======

Yahoo!路線情報から区間を指定して最安値を取得するAPIを作るプロジェクトです。  
※本プロジェクトは開発中のもので極めて不完全です

Heroku上にアップされています。  
http://thawing-ocean-4235.herokuapp.com/json/大宮/六本木  

    {"status":"200","fare":"610","url":"http://transit.loco.yahoo.co.jp/search/result?from=%E5%A4%A7%E5%AE%AE&flatlon=&to=%E5%85%AD%E6%9C%AC%E6%9C%A8&via=&expkind=1&ym=201303&d=20&datepicker=&hh=10&m1=1&m2=1&type=1&ws=2&s=1&x=101&y=12&kw="}

環境
====

Java + PlayFrameworkで作られており、Heroku上で動作するようになっています。


JSONスキーマ
============

|要素  |値            |備考                                  |
|:-----|:-------------|:-------------------------------------|
|status|HTTPステータス|-                                     |
|fare  |最安値(例:160)|取得できなかった場合は-1              |
|url   |取得元のURL   |fareが取得できなかった場合の利用を想定|
