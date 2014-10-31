//
//  QSWaterFallCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterFallCollectionViewCell.h"

@implementation QSWaterFallCollectionViewCell

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    
    if (self)
    {
        self.imageView=[[UIImageView alloc]init];
        [self addSubview:self.imageView];
        
        //--------------
        
        //透明栏
        
        //--------------
        
        float h=30;
        float x=0;
        float w=CGRectGetWidth(frame);
        float y=0;
        self.bottomBar=[[UIImageView alloc]initWithFrame:CGRectMake(x, y, w, h)];
        [self addSubview:self.bottomBar];
        
        //产品名
        y=0;
        float tempH=h/2;
        x=3;
        self.productNameLbl=[[UILabel alloc]initWithFrame:CGRectMake(x, y, w, tempH)];
        self.productNameLbl.backgroundColor=[UIColor clearColor];
        [self.bottomBar addSubview:self.productNameLbl];
        //产品价格
        y+=tempH;
        self.priceLbl=[[UILabel alloc]initWithFrame:CGRectMake(x, y, w, tempH)];
        self.priceLbl.textColor=[UIColor whiteColor];
        self.priceLbl.backgroundColor=[UIColor clearColor];
        self. priceLbl.font=[UIFont systemFontOfSize:12];
        [self.bottomBar addSubview:self.priceLbl];
    }
    
    return self;
}

@end
