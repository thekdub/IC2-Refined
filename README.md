# IC2 Refined
Refined version of IndustrialCraft 2 focused on increased performance and fewer bugs. Optimized for Tekkit Classic 3.1.2. For server use only.

Changes:
- Luminator update frequency decreased. Correction of flickering bug.
- Geothermal Generator internal storage increased to 24,000 EU from 20 EU. Only turns on after internal storage is drained. Automatically turns off after internal storage is filled. Correction of flickering bug.
- Reactor Chamber reactor caching.
- Cropmatron auto removal of stacked Weed-Ex.
- (PLANNED) EnergyNet rewritten to reduce load and remove excessive pathfinding. Cable EU loss removal.
- Transformer energy output increased 128x:
    > HV Transformer - 262,144 EU/t instead of 2048 EU/t.\
    MV Transformer - 65,536 EU/t instead of 512 EU/t.\
    LV Transformer - 16,384 EU/t instead of 128 EU/t.\
    All transformers still output their regular 512 EU (2048 EU inverted), 128 EU (512 EU inverted), and 32 EU (128 EU inverted) packets respectively. The number of packets sent has been increased.

The original IndustrialCraft 2 was made by sfPlayer1. All credits to them.
