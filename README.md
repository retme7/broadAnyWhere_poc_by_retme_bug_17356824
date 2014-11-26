broadAnyWhere_poc_by_retme_bug_17356824
=======================================
PoC source code of  Android Bug: 17356824.

diff of bug:https://android.googlesource.com/platform/packages/apps/Settings/+/37b58a4%5E%21/#F0

You can send broadcast to almost ANY reciever you want,even if it's a protect-broadcast or  the reciever is a unexported/permission-limited one.

All the devices  prior to Android 5.0 are affected.

demo video:http://v.youku.com/v_show/id_XODI1NTgxMDYw.html

more info: http://blogs.360.cn/360mobile/
		
http://retme.net/index.php/2014/11/14/broadAnywhere-bug-17356824.html

UPDATE(11.26):

Baidu X-Team report this bug,the CVE number is CVE-2014-8609:
http://seclists.org/fulldisclosure/2014/Nov/81

