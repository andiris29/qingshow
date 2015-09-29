var ItemSourceType = module.exports;

ItemSourceType.Min = 1 << 1;

ItemSourceType.Taobao = 1 << 2;
ItemSourceType.Tmall = 1 << 3;
ItemSourceType.Jamy = 1 << 4;
ItemSourceType.Hm = 1 << 5;

ItemSourceType.Max = 1 << 6;

ItemSourceType.All = ItemSourceType.Taobao | ItemSourceType.Tmall | ItemSourceType.Jamy |ItemSourceType.Hm;
