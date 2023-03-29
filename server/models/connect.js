import mongoose from 'mongoose'

export default async () => {
    await mongoose.connect(process.env.DB_URI, {
        useNewUrlParser: true,
        useUnifiedTopology: true,
    })
    console.log("Connected to MongoDB")
};