import mongoose from 'mongoose'

const gallerySchema = new mongoose.Schema({
    id: mongoose.Schema.Types.ObjectId,
    email: { type: String, required: true },
    nickname: { type: String },
    title: { type: String },
    photoUrl: { type: String, required: true },
    text: { type: String, required: true },
    type: { type: Number }
},
{
    timestamps: true
});

export default mongoose.model('Gallery', gallerySchema);
