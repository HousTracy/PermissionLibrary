# PermissionLibrary

__通过将权限请求单独封装到一个Activity/Fragment中，通过注解调用用户申请权限结果后的逻辑代码__    
两种方式供君选择哦(__推荐使用Fragment的方式__)，如图

![image](https://github.com/HousTracy/PermissionLibrary/blob/master/PermissionApplication/permission.gif)

##  1.使用方式
### 1.1 使用Activity封装方式
#### 1.1.1 在Activity和Fragment中申请权限  
```
ApplyPermissionsActivity.startApplyPermission(MainActivity.this, new String[]{Manifest.permission.XXX});
```
### 1.2 使用Fragment封装方式
#### 1.2.1 在Activity中申请权限
```
ApplyPermissionManager.startApplyPermission(MainActivity.this, new String[]{Manifest.permission.XXX});
```
#### 1.2.2 在Fragment中申请权限
```
ApplyPermissionManager.startApplyPermission(MainFragment.this, new String[]{Manifest.permission.XXX});
```
#### 1.2.3 在Adapter或者其他类中申请权限
```
ApplyPermissionManager.startApplyPermission(MainActivity.this, TestListAdapter.this, new String[]{Manifest.permission.XXX});
```
##  2.回调
### 2.1 申请成功时
```
    @PermissionGranted
    private void call() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:XXXXX"));
        startActivity(intent);
    }
```
### 2.1 申请失败时
```
   @PermissionDenied
    private void permissionDenied() {
        Toast.makeText(getApplicationContext(), "activity来处理拒绝", Toast.LENGTH_LONG).show();
    }
```
## 2.3 其他情况
为了满足同一个页面不同动作申请不同权限的场景，你可以通过如下方式区分  
调用时：
```
ApplyPermissionsActivity.startApplyPermission(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},这里添加一个requestCode);
```
最后一个参数是一个requestCode。
回调时：
```
    @PermissionGranted(code = 你的requestCode)
    private void call() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:XXXXX"));
        startActivity(intent);
    }
```


