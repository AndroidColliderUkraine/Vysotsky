package com.androidcollider.vysotsky.utils;

/**
 * Created by Пархоменко on 22.01.2015.
 */
public class AccordUtil {

    public static String[] major = new String[]{"С","С#","D","D#","E","F","F#","G","G#","A","A#","H"};
    public static String[] minor= new String[]{"Сm","С#m","Dm","D#m","Em","Fm","F#m","Gm","G#m","Am","A#m","Hm"};
    public static String[] sept = new String[]{"C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","H7"};

    public static String[] major_b = new String[]{"С","Db","D","Eb","E","F","Gb","G","Ab","A","Bb","B"};
    public static String[] minor_b= new String[]{"Сm","Dbm","Dm","Ebm","Em","Fm","Gbm","Gm","Abm","Am","Bbm","Bm"};
    public static String[] sept_b = new String[]{"C7","Db7","D7","Eb7","E7","F7","Gb7","G7","Ab7","A7","Bb7","B7"};

    public static String downAccord(String text){
        StringBuffer textBuffer = new StringBuffer(text);
        int p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<major.length; i++)
            {
                String for_find = ">"+major[i]+"<";
                int a= textBuffer.indexOf(for_find,0);// Находить перше входження рядка for_find в рядку in_str, починают з 0 символу
                if(a>=0){
                    p=a;
                    String next;
                    if(i==0){
                        next = major[11];
                    }else{
                        next = major[i-1];
                    }
                    next= "$"+next;         //Добавляємо симлов для уникнення повторного транспонування
                    textBuffer.delete(a + 1, a + 1 + major[i].length());//Видаляє попередній акорд
                    textBuffer.insert(a + 1, next);  //  Вставляє новий акорд
                }
            }
        }

        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<minor.length; i++)
            {
                String for_find = ">"+minor[i]+"<";
                int a= textBuffer.indexOf(for_find,0);// Находить перше входження рядка for_find в рядку in_str, починают з 0 символу
                if(a>=0){
                    p=a;
                    String next;
                    if(i==0){
                        next = minor[11];
                    }else{
                        next = minor[i-1];
                    }
                    next= "$"+next;         //Добавляємо симлов для уникнення повторного транспонування
                    textBuffer.delete(a + 1, a + 1 + minor[i].length());//Видаляє попередній акорд
                    textBuffer.insert(a + 1, next);  //  Вставляє новий акорд
                }
            }
        }


        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<sept.length; i++)
            {
                String for_find = ">"+sept[i]+"<";
                int a= textBuffer.indexOf(for_find,0);// Находить перше входження рядка for_find в рядку in_str, починают з 0 символу
                if(a>=0){
                    p=a;
                    String next;
                    if(i==0){
                        next = sept[11];
                    }else{
                        next = sept[i-1];
                    }
                    next= "$"+next;         //Добавляємо симлов для уникнення повторного транспонування
                    textBuffer.delete(a + 1, a + 1 + sept[i].length());//Видаляє попередній акорд
                    textBuffer.insert(a + 1, next);  //  Вставляє новий акорд
                }
            }
        }




        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<major_b.length; i++)
            {
                String for_find = ">"+major_b[i]+"<";
                int a= textBuffer.indexOf(for_find,0);// Находить перше входження рядка for_find в рядку in_str, починают з 0 символу
                if(a>=0){
                    p=a;
                    String next;
                    if(i==0){
                        next = major_b[11];
                    }else{
                        next = major_b[i-1];
                    }
                    next= "$"+next;         //Добавляємо симлов для уникнення повторного транспонування
                    textBuffer.delete(a + 1, a + 1 + major_b[i].length());//Видаляє попередній акорд
                    textBuffer.insert(a + 1, next);  //  Вставляє новий акорд
                }
            }
        }

        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<minor_b.length; i++)
            {
                String for_find = ">"+minor_b[i]+"<";
                int a= textBuffer.indexOf(for_find,0);// Находить перше входження рядка for_find в рядку in_str, починают з 0 символу
                if(a>=0){
                    p=a;
                    String next;
                    if(i==0){
                        next = minor_b[11];
                    }else{
                        next = minor_b[i-1];
                    }
                    next= "$"+next;         //Добавляємо симлов для уникнення повторного транспонування
                    textBuffer.delete(a + 1, a + 1 + minor_b[i].length());//Видаляє попередній акорд
                    textBuffer.insert(a + 1, next);  //  Вставляє новий акорд
                }
            }
        }


        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<sept_b.length; i++)
            {
                String for_find = ">"+sept_b[i]+"<";
                int a= textBuffer.indexOf(for_find,0);// Находить перше входження рядка for_find в рядку in_str, починают з 0 символу
                if(a>=0){
                    p=a;
                    String next;
                    if(i==0){
                        next = sept_b[11];
                    }else{
                        next = sept_b[i-1];
                    }
                    next= "$"+next;         //Добавляємо симлов для уникнення повторного транспонування
                    textBuffer.delete(a + 1, a + 1 + sept_b[i].length());//Видаляє попередній акорд
                    textBuffer.insert(a + 1, next);  //  Вставляє новий акорд
                }
            }
        }

        String newText = new String(textBuffer);
        String result = newText.replace("$","");
        return result;
    }

    public static String upAccord(String text){
        StringBuffer textBuffer = new StringBuffer(text);

        int p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<major.length; i++)
            {
                String for_find = ">"+major[i]+"<";
                int a= textBuffer.indexOf(for_find,0);
                if(a>=0){
                    p=a;
                    String next;
                    if(i==11){
                        next = major[0];
                    }else{
                        next = major[i+1];
                    }
                    next= "$"+next;
                    textBuffer.delete(a+1, a+1+major[i].length());
                    textBuffer.insert(a+1,next);
                }
            }
        }

        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<minor.length; i++)
            {
                String for_find = ">"+minor[i]+"<";
                int a= textBuffer.indexOf(for_find,0);
                if(a>=0){
                    p=a;
                    String next;
                    if(i==11){
                        next = minor[0];
                    }else{
                        next = minor[i+1];
                    }
                    next= "$"+next;
                    textBuffer.delete(a+1, a+1+minor[i].length());
                    textBuffer.insert(a+1,next);
                }
            }
        }

        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<sept.length; i++)
            {
                String for_find = ">"+sept[i]+"<";
                int a= textBuffer.indexOf(for_find,0);
                if(a>=0){
                    p=a;
                    String next;
                    if(i==11){
                        next = sept[0];
                    }else{
                        next = sept[i+1];
                    }
                    next= "$"+next;
                    textBuffer.delete(a+1, a+1+sept[i].length());
                    textBuffer.insert(a+1,next);
                }
            }
        }

        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<major_b.length; i++)
            {
                String for_find = ">"+major_b[i]+"<";
                int a= textBuffer.indexOf(for_find,0);
                if(a>=0){
                    p=a;
                    String next;
                    if(i==11){
                        next = major_b[0];
                    }else{
                        next = major_b[i+1];
                    }
                    next= "$"+next;
                    textBuffer.delete(a+1, a+1+major_b[i].length());
                    textBuffer.insert(a+1,next);
                }
            }
        }

        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<minor_b.length; i++)
            {
                String for_find = ">"+minor_b[i]+"<";
                int a= textBuffer.indexOf(for_find,0);
                if(a>=0){
                    p=a;
                    String next;
                    if(i==11){
                        next = minor_b[0];
                    }else{
                        next = minor_b[i+1];
                    }
                    next= "$"+next;
                    textBuffer.delete(a+1, a+1+minor_b[i].length());
                    textBuffer.insert(a+1,next);
                }
            }
        }

        p=0;
        while(p!=-1)
        {
            p=-1;
            for(int i=0; i<sept_b.length; i++)
            {
                String for_find = ">"+sept_b[i]+"<";
                int a= textBuffer.indexOf(for_find,0);
                if(a>=0){
                    p=a;
                    String next;
                    if(i==11){
                        next = sept_b[0];
                    }else{
                        next = sept_b[i+1];
                    }
                    next= "$"+next;
                    textBuffer.delete(a+1, a+1+sept_b[i].length());
                    textBuffer.insert(a+1,next);
                }
            }
        }
        String newText = new String(textBuffer);
        String result = newText.replace("$","");
        return result;
    }
}
