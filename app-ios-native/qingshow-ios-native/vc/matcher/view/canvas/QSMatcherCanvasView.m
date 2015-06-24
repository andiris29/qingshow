//
//  QSMatcherCanvasView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherCanvasView.h"
#import "UINib+QSExtension.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSCommonUtil.h"

@interface QSMatcherCanvasView ()

@property (strong, nonatomic) NSArray* categoryArray;

@end

@implementation QSMatcherCanvasView
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherCanvasView"];
}


- (void)awakeFromNib {
    self.canvasEntityView = [@[] mutableCopy];
}

- (void)bindWithCategory:(NSArray*)category {
    self.categoryArray = category;
    
    for (int i = 0; i < self.categoryArray.count; i++) {
        float width = self.frame.size.width / 3;
        float height = self.frame.size.height  / (self.categoryArray.count / 3);
        UIImageView* imgView = [[UIImageView alloc] initWithFrame:CGRectMake(width * (i % 3), height * (i / 3), width, height)];
        imgView.contentMode = UIViewContentModeScaleAspectFit;
        imgView.layer.borderColor = [UIColor blackColor].CGColor;
        imgView.layer.borderWidth = 1.f;
        imgView.backgroundColor = [UIColor colorWithRed:0.5 green:0.5 blue:0.5 alpha:0.5];
        imgView.userInteractionEnabled = YES;
        [imgView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapView:)]];
        [self.canvasEntityView addObject:imgView];
        [self addSubview:imgView];
    }
}

- (void)setItem:(NSDictionary*)itemDict forCategory:(NSDictionary*)category {
    int index = [self.categoryArray indexOfObject:category];
    UIImageView* imgView = self.canvasEntityView[index];
    [imgView setImageFromURL:[QSItemUtil getFirstImagesUrl:itemDict]];
}
- (void)setItem:(NSDictionary *)itemDict forCategoryId:(NSString *)categoryId {
    for (NSDictionary* c in self.categoryArray) {
        if ([[QSCommonUtil getIdOrEmptyStr:c] isEqualToString:categoryId]) {
            [self setItem:itemDict forCategory:c];
            return;
        }
    }
}
#pragma mark - Gesture
- (void)didTapView:(UIGestureRecognizer*)ges {
    int index = [self.canvasEntityView indexOfObject:ges.view];
    if (index >= 0 && index < self.categoryArray.count) {
        NSDictionary* c = self.categoryArray[index];
        if ([self.delegate respondsToSelector:@selector(canvasView:didTapCategory:)]) {
            [self.delegate canvasView:self didTapCategory:c];
        }
    }
}
@end
