package com.roger.weibotool.common.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.roger.weibotool.bean.Tweet;
import com.roger.weibotool.bean.User;


public class WeiboParser
{
    public static final String TAG = "WeiboParser";

    public static ArrayList<Tweet> parseTweetArray(JSONObject response,
            String tag)
    {
        ArrayList<Tweet> list = new ArrayList<Tweet>();
        try
        {
            JSONArray statusesArray = response.getJSONArray(tag);
            Log.v(TAG, "length:" + statusesArray.length());
            for (int i = 0; i < statusesArray.length(); i++)
            {
                Tweet tweet = new Tweet();
                JSONObject tweetObj = statusesArray.getJSONObject(i);
                tweet = parseTweet(tweetObj);
                JSONObject userObj = tweetObj.getJSONObject("user");
                User user = parseUser(userObj);
                tweet.setUser(user);
                list.add(tweet);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    private static Tweet parseTweet(JSONObject tweetObj)
    {
        Tweet tweet = new Tweet();
        try
        {
            tweet.setId(tweetObj.getString("id"));
            tweet.setText(tweetObj.getString("text"));
            tweet.setCreated_at(tweetObj.getString("created_at"));
            if (tweetObj.has("thumbnail_pic"))
                tweet.setPic(tweetObj.getString("thumbnail_pic"));
            tweet.setRepostCount(tweetObj.getString("reposts_count"));
        }
        catch (JSONException e)
        {
            Log.v(TAG, "parse tweet exception");
            e.printStackTrace();
        }
        return tweet;
    }

    private static User parseUser(JSONObject userObj)
    {
        User user = new User();
        try
        {
            user.setId(userObj.getString("id"));
            user.setScreen_name(userObj.getString("screen_name"));
            user.setLocation(userObj.getString("location"));
            user.setProfile_image_url(userObj.getString("profile_image_url"));
            user.setGender(userObj.getString("gender"));
        }
        catch (JSONException e)
        {
            Log.v(TAG, "parse user exception");
            e.printStackTrace();
        }
        return user;
    }

    //TODO need test
    public static <T extends IJSONObject> T commonParse(T t, String str)
    {
        try
        {
            fillJSON(t, new JSONObject(str));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return t;
        }
    }

    public static void fillJSON(Object objectToFill, JSONObject rootObj)
    {
        try
        {
            // JSONObject rootObject = new JSONObject(str);
            Class<?> clz = objectToFill.getClass();
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields)
            {
                String fieldName = field.getName();

                if (rootObj.has(field.getName()))
                {
//                    Class<?> fieldClz = field.getClass();
                    Class<?> fieldType = field.getType();
                    fieldType.isPrimitive();
                    Log.v(TAG,fieldName+" is primitive="+fieldType.isPrimitive());
                    if (fieldType.isArray())
                    {
                        // is array
                        Class<?> compClazz = fieldType.getComponentType();
                        JSONArray jsonArray = rootObj.getJSONArray(fieldName);
                        int length = jsonArray.length();
                        Log.v(TAG+"creating", compClazz.getSimpleName());
                        Object arrayFields = Array.newInstance(compClazz,
                                length);
                        for (int i = 0; i < length; i++)
                        {
                            if (AutoFillHelper.isPrimitiveType(fieldType))
                            {
                                String jsString = jsonArray.getString(i);
                                Object value = AutoFillHelper.getValue(
                                        compClazz, jsString);
                                Array.set(arrayFields, i, value);
                            }
                            else
                            {
                            	Log.v(TAG+"creating", compClazz.getSimpleName());
                                Object obj = compClazz.newInstance();
                                JSONObject jsObj = jsonArray.getJSONObject(i);
                                fillJSON(obj, jsObj);
                                Array.set(arrayFields, i, obj);
                            }
                        }
                    }
                    else if (AutoFillHelper.isPrimitiveType(fieldType))
                    {
                        // primitive type
                        fill(rootObj, field, fieldName, objectToFill);
                    }
                    else
                    {
                    	Log.v(TAG+"creating", fieldType.getSimpleName());
                        Object fieldObj = fieldType.newInstance();
                        JSONObject jsObj = rootObj.getJSONObject(fieldName);
                        fillJSON(fieldObj, jsObj);
                        field.setAccessible(true);
                        field.set(objectToFill, fieldObj);
                    }
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

    }

    private static void fill(JSONObject jsonObject, Field field,
            String fieldName, Object object) throws JSONException
    {
        Class<?> fieldClazz = field.getType();
        if (fieldClazz == Boolean.TYPE || fieldClazz == Boolean.class)
        {
            boolean value = jsonObject.getBoolean(fieldName);
            setValue(object, field, value);
        }
        else if (fieldClazz == String.class)
        {
            String value = jsonObject.getString(fieldName);
            setValue(object, field, value);
        }
        else if (fieldClazz == Long.class || fieldClazz == Long.TYPE)
        {
            long value = jsonObject.getLong(fieldName);
            setValue(object, field, value);
        }
        else if (fieldClazz == Integer.class || fieldClazz == Integer.TYPE)
        {
            int value = jsonObject.getInt(fieldName);
            setValue(object, field, value);
        }
        else if (fieldClazz == Double.class || fieldClazz == Double.TYPE)
        {
            double value = jsonObject.getDouble(fieldName);
            setValue(object, field, value);
        }

    }

    public static void setValue(Object object, Field field, Object value)
    {
        field.setAccessible(true);
        try
        {
            field.set(object, value);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public interface IJSONObject
    {

    }
}
