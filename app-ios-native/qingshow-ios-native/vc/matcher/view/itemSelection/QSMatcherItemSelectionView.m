//
//  QSMatcherItemSelectionView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherItemSelectionView.h"
#import "UINib+QSExtension.h"
@interface QSMatcherItemSelectionView ()

@property (assign, nonatomic) NSUInteger count;


#pragma mark - IBOutlet
@property (weak, nonatomic) IBOutlet UIScrollView* scrollView;
@property (weak, nonatomic) IBOutlet UIButton* previousBtn;
@property (weak, nonatomic) IBOutlet UIButton* nextBtn;
- (IBAction)previousBtnPressed:(id)sender;
- (IBAction)nextBtnPressed:(id)sender;

@end

@implementation QSMatcherItemSelectionView

+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherItemSelectionView"];
}

- (void)reloadData {
    
}

#pragma mark - IBAction
- (IBAction)previousBtnPressed:(id)sender {
    
}
- (IBAction)nextBtnPressed:(id)sender {
    
}
@end
