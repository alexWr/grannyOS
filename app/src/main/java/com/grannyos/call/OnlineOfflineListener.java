package com.grannyos.call;



public interface OnlineOfflineListener {

    /** This listener when user is online/offline
     *
     *  @param onlineOffline
     *  if user online == true
     */

    public void relativesOnlineOffline(boolean onlineOffline, String id);
}
