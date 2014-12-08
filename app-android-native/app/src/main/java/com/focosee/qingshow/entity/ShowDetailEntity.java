package com.focosee.qingshow.entity;

public class ShowDetailEntity extends AbsEntity {


    // Cover super property
    public static final String DEBUG_TAG = "ShowDetailEntity";


    // Public interface (used in S03ShowActivity.java)
    public String getShowVideo() {
        return (null != video) ? video : null;
    }

    public String getModelPhoto() {
        if (null != modelRef && null != modelRef.portrait)
            return modelRef.portrait;
        return null;
    }
    public String getModelName() {
        return modelRef.name;
    }

//    public String getItem(int index) {
//        return (index < itemRefs.length) ? itemRefs[index] : null;
//    }
//
//    public ArrayList<String> getItemsList() {
//        ArrayList<String> _itemsData = new ArrayList<String>();
//        for (String item : itemRefs)
//            _itemsData.add(item);
//        return _itemsData;
//    }
//
//    public String getAllItemDescription() {
//        String description = "";
//        for (RefItem item : itemRefs)
//            description += item.name;
//        return description;
//    }
//
//    public String getAge() {
//        return create;
//    }
//
//    public String[] getPosters() {
//        return posters;
//    }
//
//    public List<String> getItemUrlList() {
//        ArrayList<String> itemUrlList = new ArrayList<String>();
//        for (RefItem item : itemRefs) {
//            itemUrlList.add(item.cover);
//        }
//        return itemUrlList;
//    }
//
//    public List<String> getItemDescriptionList() {
//        ArrayList<String> itemDescriptionList = new ArrayList<String>();
//        for (RefItem item : itemRefs) {
//            itemDescriptionList.add(item.name);
//        }
//        return itemDescriptionList;
//    }


    // Inner data
    public String _id;                      // "5439f64013bf528b45f00f9a"
    public String cover;                    // "url for image source"
    public String video;                    // "/10.mp4.mp4"
    public int numLike;                  // "7777"
    public RefModel modelRef;               // "Model Object"
    public String create;                   // "2014-11-21T15:52:27.740Z"
    public RefItem[] itemRefs;          // "Item Object List"
    public String[] styles;
    public String[] posters;            // "Poster(str) List"
    public MetaDataCover coverMetadata;     // "Cover Object"
    public ShowContext __context;


    // Item object in show
    public static class RefModel extends AbsEntity {
        public String _id;
        public String name;
        public String portrait;
        public String birthtime;
        public int height;
        public int weight;
        public String[] followerRefs;
        public int __v;
        public String update;
        public String create;
        public InfoModel modelInfo;
        public String[] hairTypes;
        public int[] roles;
        public ModelContext __context;

        public static class InfoModel extends AbsEntity {
            public String status;
            public int numLikes;
        }

        public static class ModelContext {
            public Boolean followedByCurrentUser;
        }
    }

    public static class RefItem extends AbsEntity {
        public String _id;
        public int category;
        public String name;
        public String cover;
        public String source;
        public RefBrand brandRef;
        public String create;

        public static class RefBrand extends AbsEntity {
            public String _id;
            public String type;
            public String name;
            public String create;

        }
    }

    public static class MetaDataCover extends AbsEntity {
        public String cover;
        public int width;
        public int height;
    }

    public static class ShowContext {
        public int numComments;
        public int numLike;
        public Boolean likedByCurrentUser;
    }

}
