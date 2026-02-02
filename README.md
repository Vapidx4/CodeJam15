## Inspiration
As young college students, many of us find ourselves walking home late at night or commuting long distances every day, and almost everyone has witnessed, or personally experienced, the dangers of distracted driving. Texting and driving remains one of the most prevalent, yet most unregulated causes of car crashes and road injury, with Montreal taking a lead at an astounding 18% texting and driving rate. After further research, we found that the current texting and driving solutions on the market are hidden behind subscription costs, and most importantly, introduce preventative measures so annoying that the users abandon them quite quickly.

We wanted to build something that introduces soft friction to divert focus back into the road. We realized drivers won’t be inclined to use self help and prevention apps if we harshly dictate their driving habits, such as monitoring their speeding, unnecessary swerving and straight up blocking the use of their phone while driving. Instead, our approach is to solely focus on the amount of interaction they have on their phone, and not their driving habits. We don’t discourage the use of a phone for purposes like navigation and playing music, however, when the usage becomes severe is when we intervene. Our approach is inspired by Tesla Autopilot’s way of nagging distracted drivers on the road, while not trying them down to strict driving habits.

The app has live tracking of your Risk Factor, a metric measuring the frequency of distractibility, and thus your driving quality. Our app positively reinforces good driving behaviour, and encourages you to keep working hard on maintaining a good driving score! Our grand vision is to reduce the texting and driving rate in Montreal, and to target roadsharing companies like Uber or Lyft where their drivers’ focused driving qualities matter greatly. This app acts like a computer VanGuard, but in this case, as your own personal DriveGuard!

## What it does
DriveGuard confronts drivers that consistently interact with their phone while driving, as well as affecting their Risk Score (think, a driving Credit Score) on the basis of their driving behavior. 
Using the Java Accessibility Services, DriveGuard keeps track of whether the user is tapping their phone at a frequency which indicates inattentiveness on the road, texting, or scrolling at an disproportionally high rate. 
Using GPS Tracking, DriveGuard detects when a person is driving using their current speed, categorizing it as either a bus, car, or bicycle. It then tracks the frequency in which the user interacts with their phone. It is important to note that these features are able to get tracked outside the app, as it is set up to be used in the background of the phone, as an accessibility feature!
DriveGuard uses unique user experience (UX) (gradual shift from yellow-red alerts, as well as a slow progression to aggressive buzzing instilling a sense of urgency for the driver). 
## Our Approach to UX
DriveGuard uses unique user experience (UX) (gradual shift from yellow-red alerts, as well as a slow progression to aggressive buzzing instilling a sense of urgency for the driver). We wanted to instill a sense of awareness to the drivers through a holistically immersive experience. Rather than focusing simply on visual aesthetics, we focused on how we can use tactile, auditory, and visual stimulus to hold the driver accountable, leaving them more alert. 

## The Business Viability of Our Product
The business viability of DriveGuard is one of the strongest assets of this project. This is because DriveGuard calculates a Risk Factor for the driver, on the basis of their frequency of distraction. During this project, we wanted to create a Risk Factor that was scientifically and numerically accurate to the driver’s habits, as well as business viable to be used as a “Driver Risk Score” which can make strides in car insurance checks, parental controls for teenagers, as a probationary tool, and an integration on apps like Uber and Lyft to increase transparency regarding driver habits and quality. Our vision is for Risk Factors to be one of the standard approaches to indicate driver quality, like the Credit Score, for driving!



## How we built it
To make the mobile app we used AndroidX.

Android Kotlin Accessibility Services to detect system-wide interactions outside the app (typing, tapping, scrolling, unlocking)

We used React Native frontend for the analytics dashboard and UI, as well as our implementation for our eye tracking feature.

We initially tried Expo, but it does not support interaction detection outside of the app. We pivoted to native Kotlin for full event access and wrapped it into a React Native UI.

## Challenges we ran into
We were able to implement our eye tracking feature using Expo and React Native ML libraries, but unfortunately, Expo did not support our core feature, the ability for the app to run in the background if drivers' devices, and his we pivoted to React Native and Kotlin, not using Expo not Flutter as they could not allow the depth we needed for this project. 

There was also no reliable source for speed-limit data, making speeding detection impossible to gain. Instead, we pivoted to factoring in driving speed in our Risk Factor calculation, through a direct relationship.

When we had pivoted, nobody in the team had used Kotlin before, so having to pivot halfway to read and understand new documentation was especially challenging, particularly when trying to custom bridging Kotlin Native and React Native together.

## Accomplishments that we're proud of
Throughout the course of this challenge, we were able to pivot and successfully create 3 fully functional features! We implemented our phone interaction feature, which not only deduced different interactions (tapping, scrolling, typing) and creating a Risk Factors associated with exponentially as the driver continued their action, as well as aggregating speed information, and finally, our eye-tracking feature measuring the longevity of a person's eye contact away from the road. 

## What we learned
Our biggest lesson was learning to pivot and learn new and uncomfortable technologies if we know it were best for the project. This was our first experience using Android Studio and Kotlin, and understanding how to test our code, debug, and visually represent our thought process through an Android app. 

## What's next for DriveGuard
Given our initial implementation, our first priority is polishing the eye tracking feature through a React Native integration. While we currently did this through Kotlin to stay within the scope of our time limit, we plan to use speed checks based on location to account for speeding, swerving, and other dangerous road activities for a holistic set of actions. 

We absolutely want to continue working on this project beyond CodeJam and we see it being scaled into various industries, such usage on road tests, for new drivers, as well as pushing our Risk Factor as a widely accepted "Credit Score" for driving, displayed on sites like Uber, or assessed when purchasing car insurance. DriveGuard has significant potential for growth, and we can’t wait to fill the gap in such a heavily unregulated and ignored, yet just as integral, market. 
