package com.chsdk.configure;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年10月22日
 *          <p>
 */
public class UserDBHelper {
	private static final int MAX_USER_COUNT = 5;
	private static final String DB_FILE_NAME = "us_db";
	private UserDB userDb;

	public static void addUser(Context context, LoginUserInfo info) {
		UserDBHelper helper = new UserDBHelper(context);
		helper.addUser(info);
		helper.close();
	}

	public static LoginUserInfo getUser(Context context, String userName) {
		UserDBHelper helper = new UserDBHelper(context);
		LoginUserInfo info = helper.getUser(userName);
		helper.close();
		return info;
	}

	public static List<LoginUserInfo> getUserList(Context context) {
		UserDBHelper helper = new UserDBHelper(context);
		List<LoginUserInfo> list = helper.getUserList();
		helper.close();
		return list;
	}

	public static LoginUserInfo getLastLoginUser(Context context) {
		UserDBHelper helper = new UserDBHelper(context);
		LoginUserInfo info = helper.getLastLoginUser();
		helper.close();
		return info;
	}

	public static void deleteUser(Context context, String userName) {
		UserDBHelper helper = new UserDBHelper(context);
		helper.deleteUser(userName);
		helper.close();
	}

	public static void updateUser(Context context, LoginUserInfo info) {
		UserDBHelper helper = new UserDBHelper(context);
		helper.updateUser(info);
		helper.close();
	}

	private UserDBHelper(Context context) {
		userDb = new UserDB(context);
	}

	private void updateUser(LoginUserInfo info) {
		userDb.updateUser(info);
	}

	private void addUser(LoginUserInfo info) {
		userDb.insertUser(info);
	}

	private LoginUserInfo getUser(String userName) {
		return userDb.getUser(userName);
	}

	private List<LoginUserInfo> getUserList() {
		return userDb.getUserList();
	}

	private LoginUserInfo getLastLoginUser() {
		return userDb.getLastLoginUser();
	}

	private void close() {
		userDb.close();
	}

	private void deleteUser(String userName) {
		userDb.deleteUser(userName);
	}

	class UserDB extends SQLiteOpenHelper {
		private static final String USER_INFO_TABLE = "user_info";
		private static final int DB_VERSION = 4;

		private static final String USER_NAME = "name";
		private static final String USER_PWD = "pwd";
		private static final String USER_TOKEN = "token";
		private static final String USER_LOGIN_DATE = "date";
		private static final String USER_LOGIN_COUNT = "count";
		private static final String USER_USER_ID = "userid";
		private static final String USER_NICK_NAME = "nickname";
		private static final String USER_BIRTH_DAY = "birth";
		private static final String USER_IMG_URL = "imgurl";
		private static final String USER_AUTO_TOKEN = "autotoken";
		private static final String USER_ADD = "address";
		private static final String USER_QQ = "qq";
		private static final String USER_SEX = "sex";
		private static final String USER_FLAG = "flag";
		private static final String USER_BIND_PHONE = "bindphone";
		private static final String USER_FORUM_NAME= "forum_name";
		private static final String USER_IMG_MASK= "img_mask";
		private static final String USER_GROW_LEVEL= "grow_level";
		private static final String USER_GROW_NAME= "grow_name";
		private static final String USER_VIP_LEVEL= "vip_level";
		private static final String USER_VIP_NAME= "vip_name";

		public UserDB(Context context) {
			super(context, DB_FILE_NAME, null, DB_VERSION);
		}

		public LoginUserInfo getUser(String userName) {
			try {
				SQLiteDatabase db = getReadableDatabase();
				LoginUserInfo userInfo = null;
				if (db != null) {
					String sql = "select * from " + USER_INFO_TABLE + " where "
							+ USER_NAME + "='" + userName + "'";
					Cursor cursor = db.rawQuery(sql, null);
					if (cursor != null && cursor.moveToNext()) {
						userInfo = new LoginUserInfo();
						userInfo.userName = cursor.getString(cursor.getColumnIndex(USER_NAME));
						userInfo.passwd = cursor.getString(cursor.getColumnIndex(USER_PWD));
						userInfo.forum_name = cursor.getString(cursor.getColumnIndex(USER_FORUM_NAME));
						userInfo.img_mask = cursor.getString(cursor.getColumnIndex(USER_IMG_MASK));
						userInfo.grow_level = cursor.getString(cursor.getColumnIndex(USER_GROW_LEVEL));
						userInfo.grow_name =  cursor.getString(cursor.getColumnIndex(USER_GROW_NAME));
						userInfo.vip_level = cursor.getString(cursor.getColumnIndex(USER_VIP_LEVEL));
						userInfo.vip_name = cursor.getString(cursor.getColumnIndex(USER_VIP_NAME));
						userInfo.token = cursor.getString(cursor.getColumnIndex(USER_TOKEN));
						userInfo.date = cursor.getString(cursor.getColumnIndex(USER_LOGIN_DATE));
						userInfo.loginCount = cursor.getInt(cursor.getColumnIndex(USER_LOGIN_COUNT));
						userInfo.userId = cursor.getString(cursor.getColumnIndex(USER_USER_ID));
						userInfo.nickName = cursor.getString(cursor.getColumnIndex(USER_NICK_NAME));
						userInfo.birthDay = cursor.getString(cursor.getColumnIndex(USER_BIRTH_DAY));
						userInfo.imgUrl = cursor.getString(cursor.getColumnIndex(USER_IMG_URL));
						userInfo.autoToken = cursor.getString(cursor.getColumnIndex(USER_AUTO_TOKEN));
						userInfo.address = cursor.getString(cursor.getColumnIndex(USER_ADD));
						userInfo.qq = cursor.getString(cursor.getColumnIndex(USER_QQ));
						userInfo.sex = cursor.getInt(cursor.getColumnIndex(USER_SEX));
						userInfo.userFlag = cursor.getInt(cursor.getColumnIndex(USER_FLAG));
						userInfo.bindPhone = cursor.getInt(cursor.getColumnIndex(USER_BIND_PHONE));
					}

					if (cursor != null) {
						cursor.close();
					}
				}
				return userInfo;
			} catch (Exception e) {
				LogUtil.errorLog("db getUser:" + e.getMessage());
				return null;
			}
		}

		public void updateUser(LoginUserInfo info) {
			try {
				SQLiteDatabase db = getWritableDatabase();
				String sql = "update " + USER_INFO_TABLE + " set "
						+ USER_PWD + "='" + notNull(info.passwd) + "',"
						+ USER_FORUM_NAME + "='" + notNull(info.forum_name) + "',"
						+ USER_IMG_MASK + "='" + notNull(info.img_mask) + "',"
						+ USER_GROW_LEVEL + "='" + notNull(info.grow_level) + "',"
						+ USER_GROW_NAME + "='" + notNull(info.grow_name) + "',"
						+ USER_VIP_LEVEL + "='" + notNull(info.vip_level) + "',"
						+ USER_VIP_NAME + "='" + notNull(info.vip_name) + "',"
						+ USER_TOKEN + "='" + notNull(info.token) + "',"
						+ USER_LOGIN_DATE + "='" + System.currentTimeMillis() + "',"
						+ USER_LOGIN_COUNT + "='" + info.loginCount + "',"
						+ USER_USER_ID + "='" + notNull(info.userId) + "',"
						+ USER_NICK_NAME + "='" + notNull(info.nickName) + "',"
						+ USER_IMG_URL + "='" + notNull(info.imgUrl) + "',"
						+ USER_AUTO_TOKEN + "='" + notNull(info.autoToken) + "',"
						+ USER_ADD + "='" + notNull(info.address) + "',"
						+ USER_QQ + "='" + notNull(info.qq) + "',"
						+ USER_SEX + "='" + info.sex + "',"
						+ USER_FLAG + "='" + info.userFlag + "',"
						+ USER_BIND_PHONE + "='" + info.bindPhone + "',"
						+ USER_BIRTH_DAY + "='" + notNull(info.birthDay)
						+ "' where " + USER_NAME + "='" + info.userName +"'";
				db.execSQL(sql);
			} catch (Exception e) {
				LogUtil.errorLog("db updateUser:" + e.getMessage());
			}
		}

		public LoginUserInfo getLastLoginUser() {
			try {
				LoginUserInfo userInfo = null;
				SQLiteDatabase db = getReadableDatabase();
				if (db != null) {
					String sql = "select * from " + USER_INFO_TABLE
							+ " order by " + USER_LOGIN_DATE
							+ " desc limit 1";
					Cursor cursor = db.rawQuery(sql, null);
					if (cursor != null && cursor.moveToFirst()) {
						userInfo = new LoginUserInfo();
						userInfo.userName = cursor.getString(cursor.getColumnIndex(USER_NAME));
						userInfo.passwd = cursor.getString(cursor.getColumnIndex(USER_PWD));
						userInfo.forum_name = cursor.getString(cursor.getColumnIndex(USER_FORUM_NAME));
						userInfo.img_mask = cursor.getString(cursor.getColumnIndex(USER_IMG_MASK));
						userInfo.grow_level = cursor.getString(cursor.getColumnIndex(USER_GROW_LEVEL));
						userInfo.grow_name =  cursor.getString(cursor.getColumnIndex(USER_GROW_NAME));
						userInfo.vip_level = cursor.getString(cursor.getColumnIndex(USER_VIP_LEVEL));
						userInfo.vip_name = cursor.getString(cursor.getColumnIndex(USER_VIP_NAME));
						userInfo.token = cursor.getString(cursor.getColumnIndex(USER_TOKEN));
						userInfo.date = cursor.getString(cursor.getColumnIndex(USER_LOGIN_DATE));
						userInfo.loginCount = cursor.getInt(cursor.getColumnIndex(USER_LOGIN_COUNT));
						userInfo.userId = cursor.getString(cursor.getColumnIndex(USER_USER_ID));
						userInfo.nickName = cursor.getString(cursor.getColumnIndex(USER_NICK_NAME));
						userInfo.birthDay = cursor.getString(cursor.getColumnIndex(USER_BIRTH_DAY));
						userInfo.imgUrl = cursor.getString(cursor.getColumnIndex(USER_IMG_URL));
						userInfo.autoToken = cursor.getString(cursor.getColumnIndex(USER_AUTO_TOKEN));
						userInfo.address = cursor.getString(cursor.getColumnIndex(USER_ADD));
						userInfo.qq = cursor.getString(cursor.getColumnIndex(USER_QQ));
						userInfo.sex = cursor.getInt(cursor.getColumnIndex(USER_SEX));
						userInfo.userFlag = cursor.getInt(cursor.getColumnIndex(USER_FLAG));
						userInfo.bindPhone = cursor.getInt(cursor.getColumnIndex(USER_BIND_PHONE));
					}
					if (cursor != null) {
						cursor.close();;
					}
				}
				return userInfo;
			} catch (Exception e) {
				LogUtil.errorLog("db getLastLoginUser:" + e.getMessage());
				return null;
			}
		}

		public List<LoginUserInfo> getUserList() {
			try {
				List<LoginUserInfo> infos = null;
				SQLiteDatabase db = getReadableDatabase();
				if (db != null) {
					String sql = "select * from " + USER_INFO_TABLE
							+ " order by " + USER_LOGIN_DATE
							+ " desc limit 10";
					Cursor cursor = db.rawQuery(sql, null);
					while (cursor != null && cursor.moveToNext()) {
						LoginUserInfo userInfo = new LoginUserInfo();
						userInfo.userName = cursor.getString(cursor.getColumnIndex(USER_NAME));
						userInfo.passwd = cursor.getString(cursor.getColumnIndex(USER_PWD));
						userInfo.forum_name = cursor.getString(cursor.getColumnIndex(USER_FORUM_NAME));
						userInfo.img_mask = cursor.getString(cursor.getColumnIndex(USER_IMG_MASK));
						userInfo.grow_level = cursor.getString(cursor.getColumnIndex(USER_GROW_LEVEL));
						userInfo.grow_name =  cursor.getString(cursor.getColumnIndex(USER_GROW_NAME));
						userInfo.vip_level = cursor.getString(cursor.getColumnIndex(USER_VIP_LEVEL));
						userInfo.vip_name = cursor.getString(cursor.getColumnIndex(USER_VIP_NAME));
						userInfo.token = cursor.getString(cursor.getColumnIndex(USER_TOKEN));
						userInfo.date = cursor.getString(cursor.getColumnIndex(USER_LOGIN_DATE));
						userInfo.loginCount = cursor.getInt(cursor.getColumnIndex(USER_LOGIN_COUNT));
						userInfo.userId = cursor.getString(cursor.getColumnIndex(USER_USER_ID));
						userInfo.nickName = cursor.getString(cursor.getColumnIndex(USER_NICK_NAME));
						userInfo.birthDay = cursor.getString(cursor.getColumnIndex(USER_BIRTH_DAY));
						userInfo.imgUrl = cursor.getString(cursor.getColumnIndex(USER_IMG_URL));
						userInfo.autoToken = cursor.getString(cursor.getColumnIndex(USER_AUTO_TOKEN));
						userInfo.address = cursor.getString(cursor.getColumnIndex(USER_ADD));
						userInfo.qq = cursor.getString(cursor.getColumnIndex(USER_QQ));
						userInfo.sex = cursor.getInt(cursor.getColumnIndex(USER_SEX));
						userInfo.userFlag = cursor.getInt(cursor.getColumnIndex(USER_FLAG));
						userInfo.bindPhone = cursor.getInt(cursor.getColumnIndex(USER_BIND_PHONE));
						if (infos == null) {
							infos = new ArrayList<LoginUserInfo>();
						}
						infos.add(userInfo);
					}
					if (cursor != null) {
						cursor.close();
					}
				}
				return infos;
			} catch (Exception e) {
				LogUtil.errorLog("db getUserList:" + e.getMessage());
				return null;
			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				String sql = "create table " +  USER_INFO_TABLE + " ("
						+ USER_NAME + " text primary key,"
						+ USER_PWD + " text,"
						+ USER_FORUM_NAME + " text,"
						+ USER_IMG_MASK + " text,"
						+ USER_GROW_LEVEL + " text,"
						+ USER_GROW_NAME + " text,"
						+ USER_VIP_LEVEL + " text,"
						+ USER_VIP_NAME + " text,"
						+ USER_LOGIN_DATE + " text,"
						+ USER_LOGIN_COUNT + " int,"
						+ USER_USER_ID + " text,"
						+ USER_NICK_NAME + " text,"
						+ USER_BIRTH_DAY + " text,"
						+ USER_IMG_URL + " text,"
						+ USER_FLAG + " int,"
						+ USER_BIND_PHONE + " int,"
						+ USER_SEX + " int,"
						+ USER_ADD + " text,"
						+ USER_QQ + " text,"
						+ USER_AUTO_TOKEN + " text,"
						+ USER_TOKEN + " text)";
				db.execSQL(sql);
			} catch (Exception e) {
				LogUtil.errorLog("db onCreate:" + e.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion < newVersion) {
				checkColumn(db);
				LogUtil.errorLog("db onUpgrade: ");
			}
		}

		private void checkColumn(SQLiteDatabase db) {
			if (!checkColumnExist(db, USER_INFO_TABLE, USER_FORUM_NAME)) {
				db.execSQL("ALTER TABLE " + USER_INFO_TABLE + " ADD " + USER_FORUM_NAME + " text");
				LogUtil.errorLog("UserDBHelper checkColumn USER_FORUM_NAME ");
			}
			if (!checkColumnExist(db, USER_INFO_TABLE, USER_IMG_MASK)) {
				db.execSQL("ALTER TABLE " + USER_INFO_TABLE + " ADD " + USER_IMG_MASK + " text");
				db.execSQL("ALTER TABLE " + USER_INFO_TABLE + " ADD " + USER_GROW_LEVEL + " text");
				db.execSQL("ALTER TABLE " + USER_INFO_TABLE + " ADD " + USER_GROW_NAME + " text");
				LogUtil.errorLog("UserDBHelper checkColumn USER_IMG_MASK ");
			}
			db.execSQL("ALTER TABLE " + USER_INFO_TABLE + " ADD " + USER_VIP_LEVEL + " text");
			db.execSQL("ALTER TABLE " + USER_INFO_TABLE + " ADD " + USER_VIP_NAME + " text");
		}

        private boolean checkColumnExist(SQLiteDatabase db, String tableName
                , String columnName) {
            Cursor cursor = null;
            boolean result = false;
            try {
                cursor = db.rawQuery("select * from " + tableName + " limit 0", null);
                result = cursor != null && cursor.getColumnIndex(columnName) != -1;
            } catch (Exception e) {
                LogUtil.errorLog("UserDBHelper checkColumnExist: " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return result;
        }

		public synchronized void deleteUser(String userName) {
			try {
				SQLiteDatabase db = getWritableDatabase();
				db.delete(USER_INFO_TABLE, USER_NAME + "=?", new String[]{userName});
			} catch (Exception e) {
				LogUtil.errorLog("db deleteUser:" + e.getMessage());
			}
		}

		public synchronized void insertUser(LoginUserInfo info) {
			try {
				SQLiteDatabase db = getWritableDatabase();

				String userName = info.userName;
				String querySql = "select * from " + USER_INFO_TABLE
						+ " where " + USER_NAME + " = ?";
				Cursor cursor = db.rawQuery(querySql, new String[]{userName});
				if (cursor != null && cursor.moveToFirst()) {
					if (cursor.getCount() > 0) {
						return;
					}
				}

				if (cursor != null) {
					cursor.close();
				}

				querySql = "select * from " + USER_INFO_TABLE;
				cursor = db.rawQuery(querySql, null);
				if (cursor != null && cursor.moveToFirst()) {
					if (cursor.getCount() >= MAX_USER_COUNT) {
						querySql = "delete from " + USER_INFO_TABLE + " where "	+ USER_NAME
								+ " like (select " + USER_NAME + " from " + USER_INFO_TABLE + " order by "
								+ USER_LOGIN_DATE + " asc limit 1)";
						db.execSQL(querySql);
					}
				}

				if (cursor != null) {
					cursor.close();
				}

				String insertSql = "insert into " + USER_INFO_TABLE + "("
						+ USER_NAME + "," + USER_PWD + "," + USER_FORUM_NAME + "," + USER_IMG_MASK + "," + USER_GROW_LEVEL + "," + USER_GROW_NAME + "," + USER_TOKEN + "," + USER_VIP_LEVEL + "," + USER_VIP_NAME + ","
						+ USER_LOGIN_DATE + "," + USER_LOGIN_COUNT + ","
						+ USER_USER_ID + "," + USER_NICK_NAME + ","
						+ USER_BIRTH_DAY + "," + USER_IMG_URL + ","
						+ USER_QQ + "," + USER_ADD + "," + USER_SEX + ","
						+ USER_FLAG + "," + USER_BIND_PHONE + ","
						+ USER_AUTO_TOKEN	+ ") values ('"
						+ userName + "','" + notNull(info.passwd) + "','" + notNull(info.forum_name) + "','" + notNull(info.img_mask) + "','" + notNull(info.grow_level) + "','" + notNull(info.grow_name) + "','" + notNull(info.token) + "','" + notNull(info.vip_level) + "','" + notNull(info.vip_name) + "','"
						+ System.currentTimeMillis() + "','" + info.loginCount + "','"
						+ notNull(info.userId) + "','" + notNull(info.nickName) + "','" + notNull(info.birthDay) + "','"
						+ notNull(info.imgUrl) + "','" + notNull(info.qq) + "','" + notNull(info.address) + "','" + info.sex + "','"
						+ info.userFlag + "','" + info.bindPhone + "','"
						+ notNull(info.autoToken) + "')";
				db.execSQL(insertSql);
			} catch (Exception e) {
				LogUtil.errorLog("db insertUser:" + e.getMessage());
			}
		}

		private String notNull(String str) {
			return TextUtils.isEmpty(str) ? "" : str;
		}

		public void close() {
			try {
				SQLiteDatabase db = getWritableDatabase();
				if (db != null && db.isOpen()) {
					db.close();
				}
			} catch (Exception e) {
				LogUtil.errorLog("db close:" + e.getMessage());
			}
		}
	}
}
