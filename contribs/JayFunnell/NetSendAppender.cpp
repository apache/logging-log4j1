/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  
 */

#include <windows.h>
#include "org_apache_log4j_net_NetSendAppender.h"
#include "jni.h"

BOOL APIENTRY DllMain( HANDLE hModule, 
                       DWORD  ul_reason_for_call, 
                       LPVOID lpReserved
					 )
{
    return TRUE;
}

JNIEXPORT jboolean JNICALL Java_org_apache_log4j_net_NetSendAppender_sendMessage(JNIEnv * env, jobject obj, jstring c, jstring f,jstring t, jstring m)
{
    
    BOOL success=false;
    DWORD msgLength;
    DWORD bytesWritten;

    const char * computer=env->GetStringUTFChars(c,0);
    const char * from=env->GetStringUTFChars(f,0);
    const char * to=env->GetStringUTFChars(t,0);
    const char * message=env->GetStringUTFChars(m,0);
    
    //Prepearing WinPopup message

    msgLength=lstrlen(from)+lstrlen(to)+lstrlen(message)+4;
    char * winPopMsg=new char[msgLength];
    lstrcpy(winPopMsg,from);
    lstrcpy(winPopMsg+lstrlen(from)+1,to);
    lstrcpy(winPopMsg+lstrlen(from)+1+lstrlen(to)+1,message);

    //Prepearing Mailslot
    
    char mailslot[256];
    lstrcpy(mailslot,"\\\\");
    lstrcat(mailslot,computer);
    lstrcat(mailslot,"\\mailslot\\messngr");

    //Opening Mailslot
    
    HANDLE fileHandle=CreateFile(mailslot,GENERIC_WRITE,0,NULL,OPEN_EXISTING,0,NULL);
    if(fileHandle!=NULL)
         success=WriteFile(fileHandle,winPopMsg,msgLength,&bytesWritten,NULL);

    //Cleaning 

    delete winPopMsg;
    env->ReleaseStringUTFChars(c,computer);
    env->ReleaseStringUTFChars(f,from);
    env->ReleaseStringUTFChars(t,to);
    env->ReleaseStringUTFChars(m,message);

    if(success)
         return JNI_TRUE;
    else
         return JNI_FALSE;

}
