//
//  QSMatcherCanvasView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSMatcherCanvasView;

@protocol QSMatcherCanvasViewDelegate <NSObject>

- (void)canvasView:(QSMatcherCanvasView*)view didTapCategory:(NSDictionary*)categoryDict;

@end

@interface QSMatcherCanvasView : UIView

+ (instancetype)generateView;
- (void)bindWithCategory:(NSArray*)category;
- (void)setItem:(NSDictionary*)itemDict forCategory:(NSDictionary*)category;
- (void)setItem:(NSDictionary *)itemDict forCategoryId:(NSString *)categoryId;

- (UIImage*)submitView;

@property (strong, nonatomic) NSMutableArray* canvasEntityView;
@property (weak, nonatomic) NSObject<QSMatcherCanvasViewDelegate>* delegate;
@end
