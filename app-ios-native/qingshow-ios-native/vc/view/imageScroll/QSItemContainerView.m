//
//  QSItemContainerView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemContainerView.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSItemContainerView ()

@property (strong, nonatomic) IBOutletCollection(UIImageView) NSArray *imageViewArray;

@end

@implementation QSItemContainerView

+ (QSItemContainerView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSItemContainerView" bundle:nil];
    return [nib instantiateWithOwner:self options:nil][0];
}

- (void)bindWithImageUrl:(NSArray*)imageUrlArray
{
    for (int i = 0; i < self.imageViewArray.count; i++)
    {
        UIImageView* imgView = self.imageViewArray[i];
        if (i < imageUrlArray.count) {
            NSURL* url = imageUrlArray[i];
            [imgView setImageFromURL:url];
            UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapGesRecognize:)];
            [imgView addGestureRecognizer:ges];
            imgView.userInteractionEnabled = YES;
        }
        else
        {
            imgView.image = nil;
        }


    }
}

- (void)tapGesRecognize:(UIGestureRecognizer*)gesture
{
    if ([self.delegate respondsToSelector:@selector(didTapImageIndex:ofView:)]) {
        int index = [self.imageViewArray indexOfObject:gesture.view];
        [self.delegate didTapImageIndex:index ofView:self];
    }
}
@end
