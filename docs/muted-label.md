# 通知するレーベルを除外したい

スパムのような動きをするレーベルがいくつか存在するため、通知時に任意のレーベルからリリースアルバムは通知から除外できるようにしている。  
除外したいレーベルがある場合は、`muted_label` テーブルにレコードを追加する。  
レーベル名は完全一致ではなく完全一致ではなく文字列を含む場合（部分一致）となる。  

```sql
set @now = NOW();

INSERT INTO muted_label (label_name, created_at, updated_at, lock_version) VALUES 
('hoge', @now, @now, 0),
('fuga', @now, @now, 0);
```

## 備考

各機能に関していずれは import / export 機能のようなものを用意したいのだが、個人で使用する分にはDB操作で事足りるため、現状はこのような形となっている。
