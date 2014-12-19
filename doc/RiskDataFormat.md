# Risk Data Format

Risk drivers are represented as one of three data types, `NUMBER`, `DISTRIBUTION`
and `EVIDENCE`.

## NUMBER

`NUMBER` is a simple integer or decimal value based on it's meaning.

## DISTRIBUTION

A `DISTRIBUTION` is an array of decimal numbers, the sum of these numbers must be equal to one.
TODO: **I have no idea what the values of these numbers mean**

## EVIDENCE

An 'EVIDENCE' data type is a pair of values [positive, negative], where the 'positive' part indicates the truth value of a certain fact, while the 'negative' part indicate its falsity degree. E.g., the sentence "today there was a good weather" can be true (positive evidence) with a value 0.8, because most of the day it was sunny, but it can be false (negative evidence) with a value 0.7 if we consider that around nood there has been a strong storm. So the two values can hold at the same time.

Out of the two values, some other significant information can be calculated:

* **direction**: real number in the range [-1,1], which summarizes the balancing of positive and negative values. 
Calculated as: positive - negative.
So in the "good weather example", overall we can say that positive evidence slightly overcomes the onegative one, so the direction is +0.1.
In the output gauge it is reflected in the rotation angle of the white circle around the center of the gauge.

* **conflict**: when positive and negative evidence hold at the same time, this value indicates the degree to which they overlap. 
Calculated as: max(positive, negative) - abs(positive - negative)
So in the "good weather example", we can say that the negative evidence erodes most of the positive one; we say that the conflict value is 0.7.
In the output gauge it is reflected in the shadowed ring around the white circle.

* **strength**: summarizes in une sigle number the everall amount of information carried by positive and negative values. 
Calculated as: abs( positive + negative - positive * negative )
In the output gauge it is refleted in the distance between the white circle and the center of the gauge.

* **signal**: normalizes positive and negative values in one real in the range [0,1].
Calculated as: (1 + direction) /2
Currently not visualized in the output gauge.

