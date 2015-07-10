//
//  QSMatcherItemSelectionView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherItemPageSelectionView.h"
#import "UINib+QSExtension.h"
#import "QSMatcherItemPageSelectionImageView.h"
#define DIVIDER_X_WIDTH 4.f
#define ITEM_WIDTH 60.f
#define ITEM_PER_PAGE 5
@interface QSMatcherItemPageSelectionView ()

@property (assign, nonatomic) int count;
@property (assign, nonatomic) int currentIndex;

#pragma mark - IBOutlet
@property (weak, nonatomic) IBOutlet UIScrollView* scrollView;
@property (weak, nonatomic) IBOutlet UIButton* previousBtn;
@property (weak, nonatomic) IBOutlet UIButton* nextBtn;
- (IBAction)previousBtnPressed:(id)sender;
- (IBAction)nextBtnPressed:(id)sender;


@property (strong, nonatomic) NSArray* itemImageViews;

@end

@implementation QSMatcherItemPageSelectionView

@synthesize datasource;
@synthesize delegate;
@synthesize selectIndex;

#pragma mark - Init
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherItemPageSelectionView"];
}
- (void)awakeFromNib {
    self.currentIndex = 0;

    NSMutableArray* array = [@[] mutableCopy];
    for (int i = 0; i < ITEM_PER_PAGE * 3; i++) {
        QSMatcherItemPageSelectionImageView* v = [QSMatcherItemPageSelectionImageView generateView];
        [v addTarget:self action:@selector(didClick:) forControlEvents:UIControlEventTouchUpInside];
        [array addObject:v];
        [self.scrollView addSubview:v];
    }
    self.itemImageViews = array;
    self.scrollView.delegate = self;
}

#pragma mark - Layout
- (void)layoutSubviews {
    [super layoutSubviews];
    //update layout using autoresizing
    
    //Update scroll view content size
    CGRect bounds = self.scrollView.bounds;
    if (self.count > ITEM_PER_PAGE) {
        self.scrollView.contentSize = CGSizeMake(bounds.size.width * 3, bounds.size.height);
    } else {
        self.scrollView.contentSize = CGSizeMake(bounds.size.width, bounds.size.height);
    }

    self.scrollView.contentOffset = [self getOffset];
    
    //Update item image view

    CGFloat width = ITEM_WIDTH;
    CGFloat dividerWidhth = (bounds.size.width - ITEM_PER_PAGE * width) / (ITEM_PER_PAGE + 1);
    CGFloat height = ITEM_WIDTH;
    for (int i = 0; i < self.itemImageViews.count; i++) {
        QSMatcherItemPageSelectionImageView* v = self.itemImageViews[i];
        int screenNumber = i / ITEM_PER_PAGE;
        int imgIndex = i % ITEM_PER_PAGE;
        CGFloat x = screenNumber * bounds.size.width + imgIndex * (width + dividerWidhth) + dividerWidhth;
        v.frame = CGRectMake(x, 0, width - 2, height);
    }
}

- (CGPoint)getOffset {
    CGRect bounds = self.scrollView.bounds;
    CGPoint p = CGPointZero;
    if (self.currentIndex == 0) {
        p = CGPointMake(0, 0);
    } else if (self.currentIndex + 3 >= self.count) {
        p = CGPointMake(bounds.size.width * 2 + 1, 0);
    } else {
        p = CGPointMake(bounds.size.width + 1, 0);
    }
    return p;
}

#pragma mark -
- (void)reloadData {
    self.count = [self.datasource numberOfItemInSelectionView:self];
    [self layoutSubviews];
    [self reloadImageAndOffset];
}

#pragma mark - IBAction
- (IBAction)previousBtnPressed:(id)sender {
    if (self.currentIndex < ITEM_PER_PAGE) {
        return;
    }
    self.previousBtn.userInteractionEnabled = NO;
    [UIView animateWithDuration:0.2f animations:^{
        self.scrollView.contentOffset = CGPointMake(self.scrollView.contentOffset.x - self.scrollView.bounds.size.width, 0);
    } completion:^(BOOL finished) {
        self.previousBtn.userInteractionEnabled = YES;
        self.currentIndex -= ITEM_PER_PAGE;
        [self reloadImageAndOffset];
        [self checkFinish];
    }];

}
- (IBAction)nextBtnPressed:(id)sender {
    if (self.currentIndex + ITEM_PER_PAGE >= self.count) {
        return;
    }
    self.nextBtn.userInteractionEnabled = NO;
    [UIView animateWithDuration:0.2f animations:^{
        self.scrollView.contentOffset = CGPointMake(self.scrollView.contentOffset.x + self.scrollView.bounds.size.width, 0);
    } completion:^(BOOL finished) {
        self.nextBtn.userInteractionEnabled = YES;
        self.currentIndex += ITEM_PER_PAGE;
        [self reloadImageAndOffset];
        [self checkFinish];
    }];
    

}

#pragma mark - ScrollView Delegate
- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    if (!decelerate) {
        //case decelerate == true handle in scrollViewDidEndDecelerating
        [self updateForDraging];
    }
}
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    [self updateForDraging];
}
- (void)updateForDraging {
    int deltaIndex = (self.scrollView.contentOffset.x / self.scrollView.bounds.size.width - 1) * ITEM_PER_PAGE;
    if (self.currentIndex == 0) {
        deltaIndex += ITEM_PER_PAGE;
    } else if (self.currentIndex + ITEM_PER_PAGE >= self.count) {
        deltaIndex -= ITEM_PER_PAGE;
    }
    
    if (self.currentIndex < -deltaIndex || self.currentIndex + deltaIndex >= self.count) {
        return;
    }
    
    self.currentIndex += deltaIndex;
    [self reloadImageAndOffset];
    [self checkFinish];
}

#pragma mark - Reload
- (void)reloadImageAndOffset {
    [self.scrollView setContentOffset:[self getOffset] animated:NO];
    [self updateImage];
    [self updateBtnColor];
}

- (void)updateBtnColor {
    if (self.currentIndex == 0) {
        [self.previousBtn setImage:[UIImage imageNamed:@"matcher_item_selection_pre"] forState:UIControlStateNormal];
    } else {
        [self.previousBtn setImage:[UIImage imageNamed:@"matcher_item_selection_pre_hover"] forState:UIControlStateNormal];
    }
    if (self.currentIndex + ITEM_PER_PAGE >= self.count) {
        [self.nextBtn setImage:[UIImage imageNamed:@"matcher_item_selection_next"] forState:UIControlStateNormal];
    } else {
        [self.nextBtn setImage:[UIImage imageNamed:@"matcher_item_selection_next_hover"] forState:UIControlStateNormal];
    }
}

- (void)updateImage {
    int indexFrom = 0;
    if (self.currentIndex == 0) {
        indexFrom = self.currentIndex;
    } else if (self.currentIndex + ITEM_PER_PAGE >= self.count) {
        indexFrom = self.currentIndex - ITEM_PER_PAGE * 2;
        //此时indexFrom有可能为-3，及第一页完全空白，第二页从0开始，第三页到底
    } else {
        indexFrom = self.currentIndex - ITEM_PER_PAGE;
    }
    
    for (int i = 0; i < self.itemImageViews.count; i++) {
        int itemIndex = indexFrom + i;
        QSMatcherItemPageSelectionImageView* v = self.itemImageViews[i];
        if (itemIndex >= 0 && itemIndex < self.count) {
            NSDictionary* itemDict = [self.datasource selectionView:self itemDictAtIndex:itemIndex];
            [v bindWithItem:itemDict];
            v.hidden = NO;
        } else {
            [v bindWithItem:nil];
            v.hidden = YES;
        }
        v.hovered = itemIndex == self.selectIndex;
    }
}


- (void)didClick:(QSMatcherItemPageSelectionImageView*)imgView {
    for (QSMatcherItemPageSelectionImageView* v in self.itemImageViews) {
        v.hovered = v == imgView;
    }
    int index = [self.itemImageViews indexOfObject:imgView];
    int baseIndex =self.scrollView.contentOffset.x / self.scrollView.bounds.size.width * ITEM_PER_PAGE;
    int actualIndex = self.currentIndex - baseIndex + index;
    
    self.selectIndex = actualIndex;
    if ([self.delegate respondsToSelector:@selector(selectionView:didSelectItemAtIndex:)]) {
        [self.delegate selectionView:self didSelectItemAtIndex:actualIndex];
    }
}

- (void)checkFinish {
    if (self.currentIndex + ITEM_PER_PAGE >= self.count) {
        if ([self.delegate respondsToSelector:@selector(selectionViewDidReachEnd:)]) {
            [self.delegate selectionViewDidReachEnd:self];
        }
    }
}

- (void)offsetToZero:(BOOL)fAnimate{
    self.currentIndex = 0;
    [self.scrollView setContentOffset:[self getOffset] animated:fAnimate];
}
@end
