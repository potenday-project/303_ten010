import mongoose from 'mongoose'

const gallerySchema = new mongoose.Schema({
    id: mongoose.Schema.Types.ObjectId,
    email: { type: String, required: true },
    photoUrl: { type: String, required: true },
    text: { type: String, required: true },
},
{
    timestamps: true
});

export default mongoose.model('Gallery', gallerySchema);
